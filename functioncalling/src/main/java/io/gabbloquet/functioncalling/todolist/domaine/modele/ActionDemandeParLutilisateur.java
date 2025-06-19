package io.gabbloquet.functioncalling.todolist.domaine.modele;

import com.fasterxml.jackson.databind.JsonNode;

public record ActionDemandeParLutilisateur(
        String nomDeLaFonction,
        JsonNode argumentsDeLaFonction
) {}
