package cli.agenda.tasks.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private final String id;
    private final String text;
    private final LocalDateTime dueDate;
    private final Priority priority;
    private final Status status;
    private final LocalDateTime createdAt;

    private Task(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.text = builder.text;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;  // ← Ja ve amb el valor per defecte del Builder
        this.status = builder.status;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public Task withCompleted() {
        return new Builder(this)
                .status(Status.COMPLETED)
                .build();
    }

    public Task withUpdated(String text, LocalDateTime dueDate, Priority priority) {
        return new Builder(this)
                .text(text)
                .dueDate(dueDate)
                .priority(priority)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private String id;
        private String text;
        private LocalDateTime dueDate;
        private Priority priority = Priority.MEDIUM;
        private Status status = Status.PENDING;
        private LocalDateTime createdAt;

        public Builder() {}

        public Builder(Task task) {
            this.id = task.id;
            this.text = task.text;
            this.dueDate = task.dueDate;
            this.priority = task.priority;
            this.status = task.status;
            this.createdAt = task.createdAt;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder priority(Priority priority) {
            if (priority != null) {
                this.priority = priority;
            }
            return this;
        }

        public Builder status(Status status) {
            if (status != null) {
                this.status = status;
            }
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Task build() {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Task text cannot be empty");
            }

            return new Task(this);
        }
    }
}