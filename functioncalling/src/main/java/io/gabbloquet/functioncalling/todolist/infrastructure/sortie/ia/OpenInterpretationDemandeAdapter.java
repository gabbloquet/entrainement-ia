package io.gabbloquet.functioncalling.todolist.infrastructure.sortie.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gabbloquet.functioncalling.todolist.domaine.InterpretationDemandePort;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.DemandeDeLutilisateur;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class OpenInterpretationDemandeAdapter implements InterpretationDemandePort {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper mapper;
    private final HttpClient client;

    public OpenInterpretationDemandeAdapter(ObjectMapper mapper, HttpClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    @Override
    public ActionDemandeParLutilisateur interpreter(DemandeDeLutilisateur demande, Instant maintenant) throws Exception {
        JsonNode functions = mapper.readTree(OpenInterpretationDemandeAdapter.class.getResourceAsStream("/schema/functions.json"));

        ObjectNode discussionsAvecLia = mapper.createObjectNode();
        discussionsAvecLia.put("role", "system");
        discussionsAvecLia.put("content", "Nous sommes le " +
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        .withZone(ZoneId.of("Europe/Paris"))
                        .format(maintenant) + " et il est " +
                DateTimeFormatter.ofPattern("HH:mm:ss")
                        .withZone(ZoneId.of("Europe/Paris"))
                        .format(maintenant) + " (heure de Paris).");
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
        return new ActionDemandeParLutilisateur(nomDeLaFonction, arguments);
    }
}
