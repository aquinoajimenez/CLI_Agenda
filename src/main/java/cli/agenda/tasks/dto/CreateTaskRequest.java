package cli.agenda.tasks.dto;

import cli.agenda.tasks.model.Priority;
import java.time.LocalDateTime;

public class CreateTaskRequest {
    private final String text;
    private final LocalDateTime dueDate;
    private final Priority priority;

    public CreateTaskRequest(String text, LocalDateTime dueDate, Priority priority) {
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getText() { return text; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
}