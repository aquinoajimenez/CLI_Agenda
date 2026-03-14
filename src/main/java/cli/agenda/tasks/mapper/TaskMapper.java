package cli.agenda.tasks.mapper;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Task;

public class TaskMapper {

    private TaskMapper() {
    }

    public static Task toEntity(CreateTaskRequest request) {
        return new Task.Builder()
                .text(request.getText())
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .build();
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task);
    }
}
