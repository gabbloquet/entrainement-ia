package io.gabbloquet.functioncalling.todolist.infrastructure.entree;

import io.gabbloquet.functioncalling.todolist.AssistantOrchestrateur;
import io.gabbloquet.functioncalling.todolist.domaine.modele.DemandeDeLutilisateur;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ClientLigneDeCommande {

    private final AssistantOrchestrateur assistantOrchestrateur;

    public ClientLigneDeCommande(AssistantOrchestrateur assistantOrchestrateur) {
        this.assistantOrchestrateur = assistantOrchestrateur;
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

            assistantOrchestrateur.orchestrer(demande);
        }
    }
}
