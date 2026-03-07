package cli.agenda.tasks.repository;

import cli.agenda.tasks.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(String id);

    List<Task> findAll();

    boolean deleteById(String id);
}