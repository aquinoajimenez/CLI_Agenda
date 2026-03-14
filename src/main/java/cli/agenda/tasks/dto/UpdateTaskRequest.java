package cli.agenda.tasks.dto;

import cli.agenda.tasks.model.Priority;
import java.time.LocalDateTime;

public class UpdateTaskRequest {
    private final String id;
    private final String text;
    private final LocalDateTime dueDate;
    private final Priority priority;
    private final Boolean completed;

    public UpdateTaskRequest(String id, String text, LocalDateTime dueDate, Priority priority, Boolean completed) {
        this.id = id;
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Boolean getCompleted() { return completed; }
}