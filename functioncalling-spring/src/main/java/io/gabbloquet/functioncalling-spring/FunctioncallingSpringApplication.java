package io.gabbloquet.functioncalling;

import io.gabbloquet.functioncalling.meteo.infrastructure.entree.ClientLigneDeCommande;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FunctioncallingSpringApplication implements CommandLineRunner {

    private final ClientLigneDeCommande cli;

    public FunctioncallingSpringApplication(ClientLigneDeCommande cli) {
        this.cli = cli;
    }

    public static void main(String[] args) {
        SpringApplication.run(FunctioncallingSpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cli.lancerLaDemande();
    }
}
