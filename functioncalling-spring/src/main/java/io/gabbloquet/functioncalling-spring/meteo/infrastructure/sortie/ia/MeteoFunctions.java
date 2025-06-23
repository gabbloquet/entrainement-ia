package io.gabbloquet.functioncalling.meteo.infrastructure.sortie.ia;

import io.gabbloquet.functioncalling.meteo.domaine.MeteoPort;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeteoFunctions {

    @Autowired
    private final MeteoPort meteoPort;

    public MeteoFunctions(MeteoPort meteoPort) {
        this.meteoPort = meteoPort;
    }

    @Tool(description = "Récupère la météo d'une ville donnée")
    String recupererMeteo(@ToolParam(description = "Nom de la ville") String ville) {
        System.out.println("[Backend] : Usage de la fonction 'recupererMeteo' détectée ! (Appelé par Open AI)");
        return meteoPort.recuperer(ville);
    }

}
