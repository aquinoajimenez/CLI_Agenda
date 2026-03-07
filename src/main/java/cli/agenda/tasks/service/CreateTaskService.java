package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.exception.TaskValidationException;  // ← IMPORT QUE FALTA
import cli.agenda.tasks.mapper.TaskMapper;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import java.util.Objects;

public class CreateTaskService {

    private final TaskRepository taskRepository;

    public CreateTaskService(TaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository,
                "TaskRepository must not be null");
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        validateRequest(request);

        Task task = TaskMapper.toEntity(request);

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toResponse(savedTask);
    }

    private void validateRequest(CreateTaskRequest request) {
        if (request == null) {
            throw new TaskValidationException("Task request cannot be null");
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            throw new TaskValidationException("Task text cannot be empty");
        }
    }
}