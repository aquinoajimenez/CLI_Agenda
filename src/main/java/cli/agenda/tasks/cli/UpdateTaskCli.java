package cli.agenda.tasks.cli;

import cli.agenda.tasks.dto.UpdateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.service.UpdateTaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UpdateTaskCli {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UpdateTaskService updateTaskService;
    private final Scanner scanner;

    public UpdateTaskCli(UpdateTaskService updateTaskService, Scanner scanner) {
        this.updateTaskService = updateTaskService;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n=== UPDATE TASK ===");

        try {
            String id = getTaskId();

            String newText = getNewText();
            LocalDateTime newDueDate = getNewDueDate();
            Priority newPriority = getNewPriority();
            Boolean newCompleted = getNewCompletedStatus();

            UpdateTaskRequest request = new UpdateTaskRequest(
                    id,
                    newText,
                    newDueDate,
                    newPriority,
                    newCompleted
            );

            TaskResponse updatedTask = updateTaskService.updateTask(request);

            System.out.println("\n✅ Task updated successfully!");
            System.out.println(updatedTask);

        } catch (TaskValidationException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTaskId() {
        System.out.print("Enter task ID to update: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            throw new TaskValidationException("Task ID cannot be empty");
        }
        return id;
    }

    private String getNewText() {
        System.out.print("Enter new text (or press Enter to keep current): ");
        String text = scanner.nextLine().trim();
        return text.isEmpty() ? null : text;
    }

    private LocalDateTime getNewDueDate() {
        System.out.print("Enter new due date (yyyy-MM-dd HH:mm) or press Enter to keep current / clear: ");
        String dateInput = scanner.nextLine().trim();

        if (dateInput.isEmpty()) {
            return null;
        }

        if (dateInput.equalsIgnoreCase("clear") || dateInput.equalsIgnoreCase("null")) {
            System.out.println("   → Due date will be removed");
            return null;
        }

        try {
            return LocalDateTime.parse(dateInput, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("⚠️ Invalid date format. Keeping current due date.");
            return null;
        }
    }

    private Priority getNewPriority() {
        System.out.println("Select new priority (or press Enter to keep current):");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.print("Choice (1-3): ");

        String choice = scanner.nextLine().trim();

        return switch (choice) {
            case "1" -> Priority.LOW;
            case "2" -> Priority.MEDIUM;
            case "3" -> Priority.HIGH;
            default -> null;
        };
    }

    private Boolean getNewCompletedStatus() {
        System.out.println("Change completion status?");
        System.out.println("1. Mark as COMPLETED");
        System.out.println("2. Mark as PENDING");
        System.out.println("3. Keep current status");
        System.out.print("Choice (1-3): ");

        String choice = scanner.nextLine().trim();

        return switch (choice) {
            case "1" -> true;
            case "2" -> false;
            default -> null;
        };
    }
}