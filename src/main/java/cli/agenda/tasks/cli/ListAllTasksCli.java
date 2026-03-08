package cli.agenda.tasks.cli;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.service.ListAllTasksService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListAllTasksCli {

    private final ListAllTasksService listAllTasksService;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ListAllTasksCli(ListAllTasksService listAllTasksService) {
        this.listAllTasksService = listAllTasksService;
    }

    public void start() {
        System.out.println("\n=== ALL TASKS ===");
        System.out.println("==========================");

        List<TaskResponse> allTasks = listAllTasksService.listAllTasks();

        if (allTasks.isEmpty()) {
            System.out.println("No tasks found.");
            System.out.println("   Create your first task with option 1! ");
        } else {
            long pendingCount = allTasks.stream()
                    .filter(t -> t.getStatus() == Status.PENDING)
                    .count();
            long completedCount = allTasks.stream()
                    .filter(t -> t.getStatus() == Status.COMPLETED)
                    .count();

            System.out.println("FOUND " + allTasks.size() + " TOTAL TASKS:");
            System.out.println("   📌 PENDING: " + pendingCount + " | ✅ COMPLETED: " + completedCount + "\n");

            for (int i = 0; i < allTasks.size(); i++) {
                TaskResponse task = allTasks.get(i);

                System.out.println("─────────────────────────");
                // Status icon
                String statusIcon = task.getStatus() == Status.PENDING ? "📌" : "✅";
                System.out.println(statusIcon + " TASK #" + (i + 1) + " [" + task.getStatus() + "]");
                System.out.println("Text: " + task.getText());
                System.out.println("Priority: " + formatPriority(task.getPriority()));
                System.out.println("⏱Created: " + formatDateTime(task.getCreatedAt()));

                if (task.getDueDate() != null) {
                    System.out.println("Due date: " + formatDateTime(task.getDueDate()));
                    if (task.getStatus() == Status.PENDING) {
                        System.out.println("   " + getDueDateStatus(task.getDueDate()));
                    }
                } else {
                    System.out.println("No due date");
                }

                System.out.println("🆔 ID: " + task.getId());
            }

            System.out.println("─────────────────────────");
            System.out.println("TOTAL: " + allTasks.size() + " tasks");
            System.out.println("   📌 Pending: " + pendingCount + " | ✅ Completed: " + completedCount);
        }

        System.out.println("==========================\n");
    }

    private String formatPriority(Priority priority) {
        return switch (priority) {
            case HIGH -> "🔴 HIGH";
            case MEDIUM -> "🟡 MEDIUM";
            case LOW -> "🟢 LOW";
        };
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    private String getDueDateStatus(LocalDateTime dueDate) {
        LocalDateTime now = LocalDateTime.now();

        if (dueDate.isBefore(now)) {
            long daysOverdue = Duration.between(dueDate, now).toDays();
            return "⚠️ OVERDUE by " + daysOverdue + " days";
        } else {
            long daysLeft = Duration.between(now, dueDate).toDays();
            if (daysLeft == 0) {
                return "⏳ Due TODAY!";
            } else if (daysLeft < 3) {
                return "⏳ " + daysLeft + " days left";
            } else {
                return "✅ " + daysLeft + " days remaining";
            }
        }
    }
}