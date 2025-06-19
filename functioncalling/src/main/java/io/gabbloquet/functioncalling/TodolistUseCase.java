package io.gabbloquet.functioncalling;

import io.gabbloquet.functioncalling.domaine.modele.Tache;
import io.gabbloquet.functioncalling.domaine.TacheRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodolistUseCase {

    private final TacheRepository repository;

    public TodolistUseCase(TacheRepository repository) {
        this.repository = repository;
    }

    public String createTask(String title, String datetime) {
        try {
            LocalDateTime date = LocalDateTime.parse(datetime);
            repository.save(new Tache(title, date));
            return "Tâche ajoutée : '" + title + "' pour le " + date;
        } catch (Exception e) {
            return "Erreur lors de l'ajout de la tâche : format de date invalide.";
        }
    }

    public String listTasks() {
        List<Tache> taches = repository.findAll();
        if (taches.isEmpty()) {
            return "Aucune tâche enregistrée.";
        }
        StringBuilder sb = new StringBuilder("Tâches actuelles :\n");
        for (Tache t : taches) {
            sb.append("- ").append(t.getTitle())
              .append(" (à ").append(t.getDatetime()).append(")\n");
        }
        return sb.toString();
    }

    public String deleteTask(String title) {
        if (repository.deleteByTitle(title)) {
            return "Tâche supprimée : '" + title + "'.";
        } else {
            return "Aucune tâche trouvée avec ce titre.";
        }
    }
}
