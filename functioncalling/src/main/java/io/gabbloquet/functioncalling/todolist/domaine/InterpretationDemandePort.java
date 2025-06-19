package io.gabbloquet.functioncalling.todolist.domaine;

import io.gabbloquet.functioncalling.todolist.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.DemandeDeLutilisateur;

import java.time.Instant;

public interface InterpretationDemandePort {

    ActionDemandeParLutilisateur interpreter(DemandeDeLutilisateur demande, Instant maintenant) throws Exception;

}
