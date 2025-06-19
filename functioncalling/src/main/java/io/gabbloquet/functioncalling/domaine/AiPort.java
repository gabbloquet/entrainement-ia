package io.gabbloquet.functioncalling.domaine;

import io.gabbloquet.functioncalling.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.domaine.modele.ReponseRetravaillee;

public interface AiPort {

    ActionDemandeParLutilisateur recupererActionDemandee(DemandeDeLutilisateur demande) throws Exception;

    ReponseRetravaillee repondre(String reponseDeLaFonction, ActionDemandeParLutilisateur appel) throws Exception;

}
