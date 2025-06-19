package io.gabbloquet.functioncalling;

import io.gabbloquet.functioncalling.infrastructure.entree.ClientLigneDeCommande;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FunctioncallingApplication implements CommandLineRunner {

    private final ClientLigneDeCommande cli;

    public FunctioncallingApplication(ClientLigneDeCommande cli) {
        this.cli = cli;
    }

    public static void main(String[] args) {
        SpringApplication.run(FunctioncallingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cli.lancerLaDemande();
    }
}
