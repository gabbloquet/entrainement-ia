package io.gabbloquet.functioncalling.meteo.domaine;

import io.gabbloquet.functioncalling.meteo.domaine.modele.Meteo;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Ville;

public interface ChatMeteoPort {

    Meteo demander(Ville ville);

}
