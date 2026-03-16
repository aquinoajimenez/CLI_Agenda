package cli.agenda.tasks.service;

import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;

import java.util.Optional;

public class DeleteTaskService {

    private final TaskRepository taskRepository;

    public DeleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task deleteTask(String id) {
        System.out.println("🗑️ Deleting task with ID: " + id);

        Optional<Task> existingTaskOpt = taskRepository.findById(id);
        if (existingTaskOpt.isEmpty()) {
            throw new TaskValidationException("No task found with ID: " + id);
        }

        Task taskToDelete = existingTaskOpt.get();
        System.out.println("📋 Task to delete: " + taskToDelete.getText() +
                " [Priority: " + taskToDelete.getPriority() +
                ", Status: " + taskToDelete.getStatus() + "]");

        boolean deleted = taskRepository.deleteById(id);

        if (deleted) {
            System.out.println("✅ Task deleted successfully from repository");
            return taskToDelete;
        } else {
            throw new RuntimeException("Failed to delete task with ID: " + id);
        }
    }

    public boolean taskExists(String id) {
        return taskRepository.findById(id).isPresent();
    }
}