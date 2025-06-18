package io.gabbloquet.functioncalling.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gabbloquet.functioncalling.application.service.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.domain.model.ReponseRetravaillee;
import io.gabbloquet.functioncalling.domain.port.AiPort;
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
    public DemandeDeLutilisateur recupererFonctionEtArguments(String demandeDeLutilisateur) throws Exception {
        JsonNode functions = mapper.readTree(OpenAiAdapter.class.getResourceAsStream("/schema/functions.json"));

        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", demandeDeLutilisateur);

        ArrayNode messages = mapper.createArrayNode();
        messages.add(userMessage);

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
        String functionName = functionCall.get("name") != null ? functionCall.get("name").asText() : null;
        if (functionName == null) {
            throw new RuntimeException("Réponse IA invalide : pas de nom de fonction");
        }
        JsonNode rawArgs = functionCall.get("arguments");
        JsonNode args;
        if (rawArgs != null && rawArgs.isTextual()) {
            args = mapper.readTree(rawArgs.asText());
        } else {
            args = rawArgs;
        }
        return new DemandeDeLutilisateur(demandeDeLutilisateur, functionName, args, userMessage);
    }

    @Override
    public ReponseRetravaillee repondre(String reponseDeLaFonction, DemandeDeLutilisateur appel) throws Exception {
        ArrayNode messages = mapper.createArrayNode();
        messages.add(appel.userMessage());
        ObjectNode functionCallNode = mapper.createObjectNode();
        functionCallNode.put("name", appel.nomDeLaFonction());
        functionCallNode.put("arguments", mapper.writeValueAsString(appel.argumentsDeLaFonction()));
        ObjectNode functionCallMsg = mapper.createObjectNode();
        functionCallMsg.put("role", "assistant");
        functionCallMsg.set("function_call", functionCallNode);
        messages.add(functionCallMsg);
        ObjectNode functionMessage = mapper.createObjectNode();
        functionMessage.put("role", "function");
        functionMessage.put("name", appel.nomDeLaFonction());
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
