package io.gabbloquet.functioncalling.application.service;

import io.gabbloquet.functioncalling.domain.model.Task;
import io.gabbloquet.functioncalling.domain.port.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodolistService {

    private final TaskRepository repository;

    public TodolistService(TaskRepository repository) {
        this.repository = repository;
    }

    public String createTask(String title, String datetime) {
        try {
            LocalDateTime date = LocalDateTime.parse(datetime);
            repository.save(new Task(title, date));
            return "Tâche ajoutée : '" + title + "' pour le " + date;
        } catch (Exception e) {
            return "Erreur lors de l'ajout de la tâche : format de date invalide.";
        }
    }

    public String listTasks() {
        List<Task> tasks = repository.findAll();
        if (tasks.isEmpty()) {
            return "Aucune tâche enregistrée.";
        }
        StringBuilder sb = new StringBuilder("Tâches actuelles :\n");
        for (Task t : tasks) {
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
