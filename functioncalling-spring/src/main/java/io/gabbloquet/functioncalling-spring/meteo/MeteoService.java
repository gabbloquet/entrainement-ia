package io.gabbloquet.functioncalling.meteo;

import io.gabbloquet.functioncalling.meteo.domaine.ChatMeteoPort;
import io.gabbloquet.functioncalling.meteo.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Meteo;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Ville;
import org.springframework.stereotype.Service;

@Service
public class MeteoService {

    private final ChatMeteoPort chatMeteoPort;

    public MeteoService(ChatMeteoPort chatMeteoPort) {
        this.chatMeteoPort = chatMeteoPort;
    }

    public Meteo recuperer(DemandeDeLutilisateur demande) {
        return chatMeteoPort.demander(new Ville(demande.textuelle()));
    }
}
