package io.gabbloquet.functioncalling.infrastructure.sortie.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gabbloquet.functioncalling.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.domaine.modele.ReponseRetravaillee;
import io.gabbloquet.functioncalling.domaine.AiPort;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenAiAdapter implements AiPort {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    @Override
    public ActionDemandeParLutilisateur recupererActionDemandee(DemandeDeLutilisateur demande) throws Exception {
        JsonNode functions = mapper.readTree(OpenAiAdapter.class.getResourceAsStream("/schema/functions.json"));

        ObjectNode discussionsAvecLia = mapper.createObjectNode();
        discussionsAvecLia.put("role", "user");
        discussionsAvecLia.put("content", demande.textuelle());

        ArrayNode messages = mapper.createArrayNode();
        messages.add(discussionsAvecLia);

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-4.1-nano");
        requestBody.set("messages", messages);
        requestBody.set("functions", functions);
        requestBody.put("function_call", "auto");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());
        JsonNode choices = root.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Réponse IA invalide : pas de choix");
        }
        JsonNode choice = choices.get(0);
        JsonNode message = choice.get("message");
        if (message == null) {
            throw new RuntimeException("Réponse IA invalide : pas de message");
        }
        JsonNode functionCall = message.get("function_call");
        if (functionCall == null) {
            throw new RuntimeException("Réponse IA invalide : pas de function_call");
        }
        String nomDeLaFonction = functionCall.get("name") != null ? functionCall.get("name").asText() : null;
        if (nomDeLaFonction == null) {
            throw new RuntimeException("Réponse IA invalide : pas de nom de fonction");
        }
        JsonNode rawArgs = functionCall.get("arguments");
        JsonNode arguments;
        if (rawArgs != null && rawArgs.isTextual()) {
            arguments = mapper.readTree(rawArgs.asText());
        } else {
            arguments = rawArgs;
        }
        return new ActionDemandeParLutilisateur(nomDeLaFonction, arguments, discussionsAvecLia);
    }

    @Override
    public ReponseRetravaillee repondre(String reponseDeLaFonction, ActionDemandeParLutilisateur action) throws Exception {
        ArrayNode messages = mapper.createArrayNode();
        messages.add(action.discussionAvecLia());
        ObjectNode functionCallNode = mapper.createObjectNode();
        functionCallNode.put("name", action.nomDeLaFonction());
        functionCallNode.put("arguments", mapper.writeValueAsString(action.argumentsDeLaFonction()));
        ObjectNode functionCallMsg = mapper.createObjectNode();
        functionCallMsg.put("role", "assistant");
        functionCallMsg.set("function_call", functionCallNode);
        messages.add(functionCallMsg);
        ObjectNode functionMessage = mapper.createObjectNode();
        functionMessage.put("role", "function");
        functionMessage.put("name", action.nomDeLaFonction());
        functionMessage.put("content", reponseDeLaFonction);
        messages.add(functionMessage);

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-4.1-nano");
        requestBody.set("messages", messages);

        HttpRequest secondRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> finalResponse = client.send(secondRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode finalRoot = mapper.readTree(finalResponse.body());
        JsonNode choices = finalRoot.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Réponse IA invalide : pas de choix");
        }
        JsonNode choice = choices.get(0);
        if (choice == null) {
            throw new RuntimeException("Réponse IA invalide : pas de choix");
        }
        JsonNode message = choice.get("message");
        if (message == null) {
            throw new RuntimeException("Réponse IA invalide : pas de message");
        }
        JsonNode content = message.get("content");
        if (content == null) {
            throw new RuntimeException("Réponse IA invalide : pas de contenu");
        }
        return new ReponseRetravaillee(content.asText());
    }
}
