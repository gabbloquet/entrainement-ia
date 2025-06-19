package io.gabbloquet.functioncalling.todolist;

import io.gabbloquet.functioncalling.todolist.domaine.GenerationReponsePort;
import io.gabbloquet.functioncalling.todolist.domaine.InterpretationDemandePort;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.ReponseRetravaillee;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AssistantOrchestrateur {

    private final InterpretationDemandePort interpretationDemandePort;
    private final GenerationReponsePort generationReponsePort;
    private final TodolistUseCase todolistUseCase;

    public AssistantOrchestrateur(InterpretationDemandePort interpretationDemandePort, GenerationReponsePort generationReponsePort, TodolistUseCase todolistUseCase) {
        this.interpretationDemandePort = interpretationDemandePort;
        this.generationReponsePort = generationReponsePort;
        this.todolistUseCase = todolistUseCase;
    }

    public void orchestrer(DemandeDeLutilisateur demande) throws Exception {
        Instant maintenant = Instant.now();
        ActionDemandeParLutilisateur action = interpretationDemandePort.interpreter(demande, maintenant);

        System.out.println("[L'IA interprète la demande de l'utilisateur] => " + action.nomDeLaFonction() + " avec les arguments => " + action.argumentsDeLaFonction());

        String reponse = switch (action.nomDeLaFonction()) {
            case "create_task" -> todolistUseCase.createTask(
                    action.argumentsDeLaFonction().get("title").asText(),
                    action.argumentsDeLaFonction().get("datetime").asText()
            );
            case "delete_task" -> todolistUseCase.deleteTask(
                    action.argumentsDeLaFonction().get("title").asText()
            );
            case "list_tasks" -> todolistUseCase.listTasks();
            default -> "Fonction inconnue : " + action.nomDeLaFonction();
        };

        System.out.println("[Le service (FunctioncallingApplication) a bien traité la demande de l'utilisateur] => " + reponse);

        ReponseRetravaillee reponseRetravaillee = generationReponsePort.generer(action, reponse);

        System.out.println("[Réponse pour l'utilisateur (retravaillée avec IA)] => " + reponseRetravaillee.texte());
        System.out.println();
    }
}
