package io.gabbloquet.functioncalling.domain.port;

import io.gabbloquet.functioncalling.application.service.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.domain.model.ReponseRetravaillee;

public interface AiPort {

    DemandeDeLutilisateur recupererFonctionEtArguments(String demandeDeLutilisateur) throws Exception;

    ReponseRetravaillee repondre(String reponseDeLaFonction, DemandeDeLutilisateur appel) throws Exception;

}
