package cli.agenda.tasks.cli;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.CreateTaskService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class CreateTaskCli {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CreateTaskService createTaskService;
    private final Scanner scanner;

    public CreateTaskCli(CreateTaskService createTaskService, Scanner scanner) {
        this.createTaskService = createTaskService;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n=== Create New Task ===");

        try {
            String text = getTaskText();

            LocalDateTime dueDate = getDueDate();

            Priority priority = getPriority();

            CreateTaskRequest request = new CreateTaskRequest(text, dueDate, priority);

            TaskResponse createdTask = createTaskService.createTask(request);

            System.out.println("\nTask created successfully!");
            System.out.println(createdTask);

        } catch (TaskValidationException e) {
            System.out.println("\nValidation error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUnexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTaskText() {
        System.out.print("Enter task description (required): ");
        String text = scanner.nextLine().trim();

        if (text.isEmpty()) {
            throw new TaskValidationException("Task description cannot be empty");
        }

        return text;
    }

    private LocalDateTime getDueDate() {
        System.out.print("Enter due date (yyyy-MM-dd HH:mm) or press Enter to skip: ");
        String dateInput = scanner.nextLine().trim();

        if (dateInput.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateInput, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using no due date.");
            return null;
        }
    }

    private Priority getPriority() {
        System.out.println("Select priority (press Enter for default MEDIUM):");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM (default)");
        System.out.println("3. HIGH");
        System.out.print("Choice (1-3): ");

        String choice = scanner.nextLine().trim();

        return switch (choice) {
            case "1" -> Priority.LOW;
            case "2", "" -> Priority.MEDIUM;
            case "3" -> Priority.HIGH;
            default -> {
                System.out.println("Invalid choice. Using MEDIUM priority.");
                yield Priority.MEDIUM;
            }
        };
    }
}
