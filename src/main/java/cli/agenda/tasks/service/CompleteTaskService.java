package cli.agenda.tasks.service;

import cli.agenda.tasks.exception.TaskAlreadyCompletedException;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;

import java.util.Optional;

public class CompleteTaskService {

    private final TaskRepository taskRepository;

    public CompleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task markAsCompleted(String id) {
        System.out.println("✅ Marking task as completed. ID: " + id);

        if (id == null || id.trim().isEmpty()) {
            throw new TaskValidationException("Task ID cannot be empty");
        }

        Optional<Task> existingTaskOpt = taskRepository.findById(id);
        if (existingTaskOpt.isEmpty()) {
            throw new TaskValidationException("No task found with ID: " + id);
        }

        Task existingTask = existingTaskOpt.get();
        System.out.println("📋 Current task: " + existingTask.getText() +
                " [Status: " + existingTask.getStatus() + "]");

        if (existingTask.getStatus() == Status.COMPLETED) {
            throw new TaskAlreadyCompletedException("Task with ID " + id + " was already marked as completed.");
        }

        Task completedTask = existingTask.withCompleted();

        Task savedTask = taskRepository.save(completedTask);
        System.out.println("✅ Task marked as completed successfully");

        return savedTask;
    }

    public boolean canBeCompleted(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        Optional<Task> taskOpt = taskRepository.findById(id);
        return taskOpt.isPresent() && taskOpt.get().getStatus() != Status.COMPLETED;
    }
}