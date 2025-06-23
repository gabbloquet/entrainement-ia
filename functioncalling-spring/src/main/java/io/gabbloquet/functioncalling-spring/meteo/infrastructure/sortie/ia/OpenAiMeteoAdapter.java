package io.gabbloquet.functioncalling.meteo.infrastructure.sortie.ia;

import io.gabbloquet.functioncalling.meteo.domaine.ChatMeteoPort;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Meteo;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Ville;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenAiMeteoAdapter implements ChatMeteoPort {

    @Autowired
    private final ChatClient chatClient;
    @Autowired
    private final io.gabbloquet.functioncalling.meteo.infrastructure.sortie.ia.MeteoFunctions meteoFunctions;

    public OpenAiMeteoAdapter(ChatClient chatClient, io.gabbloquet.functioncalling.meteo.infrastructure.sortie.ia.MeteoFunctions meteoFunctions) {
        this.chatClient = chatClient;
        this.meteoFunctions = meteoFunctions;
    }

    public Meteo demander(Ville ville) {
        System.out.println("[Backend] : Chat GPT va traité la requête.");

        String response = chatClient.prompt(ville.nom())
                .tools(meteoFunctions)
                .call()
                .content();

        System.out.printf("[Chat GPT] : '%s'.\n", response);
        return new Meteo(response);
    }
}
