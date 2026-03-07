package cli.agenda.tasks.dto;

import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import java.time.LocalDateTime;

public class TaskResponse {
    private final String id;
    private final String text;
    private final LocalDateTime dueDate;
    private final Priority priority;
    private final Status status;
    private final LocalDateTime createdAt;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.text = task.getText();
        this.dueDate = task.getDueDate();
        this.priority = task.getPriority();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt();
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Task{id='%s', text='%s', dueDate=%s, priority=%s, status=%s, createdAt=%s}",
                id, text, dueDate, priority, status, createdAt);
    }
}