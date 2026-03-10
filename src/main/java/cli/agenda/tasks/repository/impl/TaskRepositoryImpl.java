package cli.agenda.tasks.repository.impl;

import cli.agenda.tasks.dao.TaskDAO;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryImpl implements TaskRepository {

    private final TaskDAO taskDAO;

    public TaskRepositoryImpl(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
        System.out.println("✅ Repository initialized with DAO");
    }

    @Override
    public Task save(Task task) {
        System.out.println("📦 Repository: Saving task");

        if (task.getId() == null || findById(task.getId()).isEmpty()) {
            return taskDAO.insert(task);
        } else {
            boolean updated = taskDAO.update(task);
            if (updated) {
                return task;
            } else {
                throw new RuntimeException("Failed to update task");
            }
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        return taskDAO.findById(id);
    }

    @Override
    public List<Task> findAll() {
        return taskDAO.findAll();
    }

    @Override
    public boolean deleteById(String id) {
        return taskDAO.deleteById(id);
    }

    @Override
    public List<Task> findByStatus(Status status) {
        return taskDAO.findByStatus(status);
    }
}
