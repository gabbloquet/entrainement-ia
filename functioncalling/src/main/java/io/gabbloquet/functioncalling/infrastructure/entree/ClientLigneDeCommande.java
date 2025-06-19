package io.gabbloquet.functioncalling.infrastructure.entree;

import io.gabbloquet.functioncalling.domaine.modele.ActionDemandeParLutilisateur;
import io.gabbloquet.functioncalling.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.TodolistUseCase;
import io.gabbloquet.functioncalling.domaine.modele.ReponseRetravaillee;
import io.gabbloquet.functioncalling.domaine.AiPort;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ClientLigneDeCommande {
    private final AiPort aiPort;
    private final TodolistUseCase todolistUseCase;

    public ClientLigneDeCommande(AiPort aiPort, TodolistUseCase todolistUseCase) {
        this.aiPort = aiPort;
        this.todolistUseCase = todolistUseCase;
    }

    public void lancerLaDemande() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Vous ➤ ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput) || "quit".equalsIgnoreCase(userInput)) {
                System.out.println("Au revoir !");
                break;
            }

            DemandeDeLutilisateur demande = new DemandeDeLutilisateur(userInput);

            ActionDemandeParLutilisateur action = aiPort.recupererActionDemandee(demande);

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

            ReponseRetravaillee reponseRetravaillee = aiPort.repondre(reponse, action);

            System.out.println("[Réponse pour l'utilisateur (retravaillée avec IA)] => " + reponseRetravaillee.texte());
            System.out.println(); // Pour la lisibilité
        }
    }
}
