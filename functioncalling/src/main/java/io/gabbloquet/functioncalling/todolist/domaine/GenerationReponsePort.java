package io.gabbloquet.functioncalling.todolist.domaine;

import io.gabbloquet.functioncalling.todolist.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ReponseRetravaillee;

public interface GenerationReponsePort {

    ReponseRetravaillee generer(ActionDemandeParLutilisateur action, String reponse) throws Exception;

}
