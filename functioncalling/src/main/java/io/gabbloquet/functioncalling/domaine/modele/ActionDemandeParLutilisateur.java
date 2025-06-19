package io.gabbloquet.functioncalling.domaine.modele;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record ActionDemandeParLutilisateur(
        String nomDeLaFonction,
        JsonNode argumentsDeLaFonction,
        ObjectNode discussionAvecLia
) {}
