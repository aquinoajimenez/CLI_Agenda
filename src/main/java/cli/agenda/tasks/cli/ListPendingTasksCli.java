package cli.agenda.tasks.cli;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.service.ListPendingTasksService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListPendingTasksCli {

    private final ListPendingTasksService listPendingTasksService;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ListPendingTasksCli(ListPendingTasksService listPendingTasksService) {
        this.listPendingTasksService = listPendingTasksService;
    }

    public void start() {
        System.out.println("\n📋 === PENDING TASKS === 📋");
        System.out.println("===============================");

        List<TaskResponse> pendingTasks = listPendingTasksService.listPendingTasks();

        if (pendingTasks.isEmpty()) {
            System.out.println("🍃 No pending tasks.");
            System.out.println("   Enjoy your free time! 😊");
        } else {
            System.out.println("✅ FOUND " + pendingTasks.size() + " PENDING TASKS:\n");

            for (int i = 0; i < pendingTasks.size(); i++) {
                TaskResponse task = pendingTasks.get(i);

                System.out.println("─────────────────────────");
                System.out.println("🔹 TASK #" + (i + 1));
                System.out.println("📝 Text: " + task.getText());
                System.out.println("🎯 Priority: " + formatPriority(task.getPriority()));
                System.out.println("⏱️ Created: " + formatDateTime(task.getCreatedAt()));

                if (task.getDueDate() != null) {
                    System.out.println("⏰ Due date: " + formatDateTime(task.getDueDate()));
                    System.out.println("   " + getDueDateStatus(task.getDueDate()));
                } else {
                    System.out.println("⏰ No due date");
                }

                System.out.println("🆔 ID: " + task.getId());
            }

            System.out.println("─────────────────────────");
            System.out.println("📊 TOTAL: " + pendingTasks.size() + " pending tasks");
        }

        System.out.println("===============================\n");
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
            return "⚠️ OVERDUE!";
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
