package io.gabbloquet.functioncalling.todolist.infrastructure.sortie.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gabbloquet.functioncalling.todolist.domaine.GenerationReponsePort;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ReponseRetravaillee;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenAiGenerationReponseAdapter implements GenerationReponsePort {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper mapper;
    private final HttpClient client;

    public OpenAiGenerationReponseAdapter(ObjectMapper mapper, HttpClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public ReponseRetravaillee generer(ActionDemandeParLutilisateur action, String reponse) throws Exception {
        ArrayNode messages = mapper.createArrayNode();

        ObjectNode userMsg = mapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", String.format(
                "Explique à l'utilisateur, en langage naturel et de façon claire, ce qui vient d'être réalisé dans l'application, " +
                        "sans jamais mentionner de notions techniques, de code, d'API, de fonction ou d'arguments. " +
                        "Adapte ton explication à tout type d'action ou de service, même si ce n'est pas lié à une tâche. " +
                        "Contexte : l'action réalisée est '%s' avec ces informations : %s. Résultat du service : %s.",
                action.nomDeLaFonction(),
                action.argumentsDeLaFonction().toString(),
                reponse
        ));
        messages.add(userMsg);

        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-4.1-nano");
        requestBody.set("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());
        JsonNode content = root.get("choices").get(0).get("message").get("content");
        return new ReponseRetravaillee(content.asText());
    }
}
