package cli.agenda.tasks.cli;

import cli.agenda.tasks.exception.TaskAlreadyCompletedException;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.service.CompleteTaskService;

import java.util.Scanner;

public class CompleteTaskCli {

    private final CompleteTaskService completeTaskService;
    private final Scanner scanner;

    public CompleteTaskCli(CompleteTaskService completeTaskService, Scanner scanner) {
        this.completeTaskService = completeTaskService;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n=== ✅ MARK TASK AS COMPLETED ===");

        try {
            String id = getTaskId();

            if (!completeTaskService.canBeCompleted(id)) {
                System.out.println("⚠️  Task cannot be marked as completed. It may not exist or is already completed.");
            }

            Task completedTask = completeTaskService.markAsCompleted(id);

            System.out.println("\n✅ Task marked as completed successfully!");
            System.out.println("   ✅ " + completedTask.getText() + " [ID: " + completedTask.getId() + "]");
            System.out.println("   📅 Completed at: " + java.time.LocalDateTime.now());

        } catch (TaskAlreadyCompletedException e) {
            System.out.println("\nℹ️ " + e.getMessage());
        } catch (TaskValidationException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTaskId() {
        System.out.print("Enter task ID to mark as completed: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            throw new TaskValidationException("Task ID cannot be empty");
        }
        return id;
    }
}
