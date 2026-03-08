package cli.agenda.tasks;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.exception.TaskValidationException;
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
        System.out.println("🚀 Iniciant creació de tasca...");

        validateRequest(request);
        System.out.println("✅ Validació superada");

        Task task = TaskMapper.toEntity(request);
        System.out.println("✅ Task entity creada amb ID: " + task.getId());
        System.out.println("📝 Text: " + task.getText());
        System.out.println("📅 DueDate: " + task.getDueDate());
        System.out.println("🎯 Priority: " + task.getPriority());
        System.out.println("⏱️ CreatedAt: " + task.getCreatedAt());

        try {
            Task savedTask = taskRepository.save(task);
            System.out.println("✅ Task guardada al repositori");

            TaskResponse response = TaskMapper.toResponse(savedTask);
            System.out.println("✅ Response generada: " + response);

            return response;

        } catch (Exception e) {
            System.err.println("❌ Error en guardar: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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