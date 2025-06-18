package io.gabbloquet.functioncalling.infrastructure.repository;

import io.gabbloquet.functioncalling.domain.model.Task;
import io.gabbloquet.functioncalling.domain.port.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class InMemoryTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void save(Task task) {
        tasks.add(task);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public boolean deleteByTitle(String title) {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.getTitle().equals(title)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}
