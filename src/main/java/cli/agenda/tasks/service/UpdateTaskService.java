package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.UpdateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.mapper.TaskMapper;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateTaskService {

    private final TaskRepository taskRepository;

    public UpdateTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse updateTask(UpdateTaskRequest request) {
        System.out.println("🔄 Updating task with ID: " + request.getId());

        Optional<Task> existingTaskOpt = taskRepository.findById(request.getId());
        if (existingTaskOpt.isEmpty()) {
            throw new TaskValidationException("No task found with ID: " + request.getId());
        }

        Task existingTask = existingTaskOpt.get();
        System.out.println("📋 Current task: " + existingTask.getText() + " [Priority: " + existingTask.getPriority() +
                ", Status: " + existingTask.getStatus() +
                (existingTask.getDueDate() != null ? ", Due: " + existingTask.getDueDate() : ""));

        String newText = request.getText() != null ? request.getText() : existingTask.getText();
        LocalDateTime newDueDate = request.getDueDate() != null ? request.getDueDate() : existingTask.getDueDate();
        Priority newPriority = request.getPriority() != null ? request.getPriority() : existingTask.getPriority();

        Task updatedTask;
        if (request.getCompleted() != null) {
            if (request.getCompleted()) {
                updatedTask = existingTask.withCompleted();
            } else {
                updatedTask = new Task.Builder(existingTask)
                        .status(Status.PENDING)
                        .build();
            }
        } else {
            updatedTask = existingTask;
        }

        if (!newText.equals(existingTask.getText()) ||
                !equalsDates(newDueDate, existingTask.getDueDate()) ||
                newPriority != existingTask.getPriority() ||
                (request.getCompleted() != null && request.getCompleted() != (existingTask.getStatus() == Status.COMPLETED))) {

            updatedTask = new Task.Builder(updatedTask)
                    .text(newText)
                    .dueDate(newDueDate)
                    .priority(newPriority)
                    .build();
        }

        validateUpdatedTask(updatedTask);

        Task savedTask = taskRepository.save(updatedTask);
        System.out.println("✅ Task updated successfully");

        return TaskMapper.toResponse(savedTask);
    }

    private void validateUpdatedTask(Task task) {
        if (task.getText() == null || task.getText().trim().isEmpty()) {
            throw new TaskValidationException("Task text cannot be empty");
        }
    }

    private boolean equalsDates(LocalDateTime d1, LocalDateTime d2) {
        if (d1 == null && d2 == null) return true;
        if (d1 == null || d2 == null) return false;
        return d1.equals(d2);
    }
}
