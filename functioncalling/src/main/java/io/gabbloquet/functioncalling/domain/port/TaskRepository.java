package io.gabbloquet.functioncalling.domain.port;

import io.gabbloquet.functioncalling.domain.model.Task;
import java.util.List;

public interface TaskRepository {

    void save(Task task);

    List<Task> findAll();

    boolean deleteByTitle(String title);
}
