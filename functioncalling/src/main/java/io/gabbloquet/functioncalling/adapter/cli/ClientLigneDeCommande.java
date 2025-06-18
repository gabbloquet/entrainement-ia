package io.gabbloquet.functioncalling.adapter.cli;

import io.gabbloquet.functioncalling.application.service.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.application.service.TodolistService;
import io.gabbloquet.functioncalling.domain.model.ReponseRetravaillee;
import io.gabbloquet.functioncalling.domain.port.AiPort;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ClientLigneDeCommande {
    private final AiPort aiPort;
    private final TodolistService todolistService;

    public ClientLigneDeCommande(AiPort aiPort, TodolistService todolistService) {
        this.aiPort = aiPort;
        this.todolistService = todolistService;
    }

    public void lancerLaDemande() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Vous ➤ ");
        String demandeDeLutilisateur = scanner.nextLine();

        DemandeDeLutilisateur demande = aiPort.recupererFonctionEtArguments(demandeDeLutilisateur);

        String reponse = switch (demande.nomDeLaFonction()) {
            case "create_task" -> todolistService.createTask(
                    demande.argumentsDeLaFonction().get("title").asText(),
                    demande.argumentsDeLaFonction().get("datetime").asText()
            );
            case "delete_task" -> todolistService.deleteTask(
                    demande.argumentsDeLaFonction().get("title").asText()
            );
            case "list_tasks" -> todolistService.listTasks();
            default -> "Fonction inconnue : " + demande.nomDeLaFonction();
        };
        System.out.println("[Résultat backend] => " + reponse);

        ReponseRetravaillee reponseRetravaillee = aiPort.repondre(reponse, demande);

        System.out.println("[Réponse pour l'utilisateur (retravaillée avec IA)] => " + reponseRetravaillee.texte());
    }
}
