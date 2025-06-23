package io.gabbloquet.functioncalling.meteo.infrastructure.entree;

import io.gabbloquet.functioncalling.meteo.MeteoService;
import io.gabbloquet.functioncalling.meteo.domaine.modele.DemandeDeLutilisateur;
import io.gabbloquet.functioncalling.meteo.domaine.modele.Meteo;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ClientLigneDeCommande {

    private final MeteoService service;

    public ClientLigneDeCommande(MeteoService service) {
        this.service = service;
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

            Meteo meteo = service.recuperer(demande);

            System.out.println(meteo.description());
        }
    }
}
