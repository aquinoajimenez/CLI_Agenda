package cli.agenda.tasks.cli;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.service.ListCompletedTasksService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListCompletedTasksCli {

    private final ListCompletedTasksService listCompletedTasksService;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ListCompletedTasksCli(ListCompletedTasksService listCompletedTasksService) {
        this.listCompletedTasksService = listCompletedTasksService;
    }

    public void start() {
        System.out.println("\n✅ === COMPLETED TASKS === ✅");
        System.out.println("================================");

        List<TaskResponse> completedTasks = listCompletedTasksService.listCompletedTasks();

        if (completedTasks.isEmpty()) {
            System.out.println("🎉 No completed tasks yet.");
            System.out.println("   Keep working! You'll get there! 💪");
        } else {
            System.out.println("✅ FOUND " + completedTasks.size() + " COMPLETED TASKS:\n");

            for (int i = 0; i < completedTasks.size(); i++) {
                TaskResponse task = completedTasks.get(i);

                System.out.println("─────────────────────────");
                System.out.println("✅ TASK #" + (i + 1));
                System.out.println("📝 Text: " + task.getText());
                System.out.println("🎯 Priority: " + formatPriority(task.getPriority()));
                System.out.println("⏱️ Created: " + formatDateTime(task.getCreatedAt()));

                if (task.getDueDate() != null) {
                    System.out.println("⏰ Due date: " + formatDateTime(task.getDueDate()));
                    System.out.println("   " + getCompletionStatus(task.getDueDate(), task.getCreatedAt()));
                } else {
                    System.out.println("⏰ No due date");
                }

                System.out.println("🆔 ID: " + task.getId());
            }

            System.out.println("─────────────────────────");
            System.out.println("📊 TOTAL: " + completedTasks.size() + " completed tasks");
            System.out.println("🏆 Great job! Keep it up!");
        }

        System.out.println("================================\n");
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

    private String getCompletionStatus(LocalDateTime dueDate, LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        if (dueDate.isBefore(now)) {
            long daysOverdue = Duration.between(dueDate, now).toDays();
            return "⚠️ Completed " + daysOverdue + " days overdue";
        } else {
            long daysEarly = Duration.between(now, dueDate).toDays();
            if (daysEarly == 0) {
                return "🎯 Completed on time!";
            } else {
                return "✨ Completed " + daysEarly + " days early";
            }
        }
    }
}
