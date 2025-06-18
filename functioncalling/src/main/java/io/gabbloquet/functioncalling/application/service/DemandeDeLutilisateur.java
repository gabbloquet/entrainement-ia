package io.gabbloquet.functioncalling.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record DemandeDeLutilisateur(
        String demandeDeLutilisateur,
        String nomDeLaFonction,
        JsonNode argumentsDeLaFonction,
        ObjectNode userMessage
) {}
