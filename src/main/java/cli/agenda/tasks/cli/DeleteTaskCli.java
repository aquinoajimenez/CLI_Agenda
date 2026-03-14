package cli.agenda.tasks.cli;

import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.service.DeleteTaskService;

import java.util.Scanner;

public class DeleteTaskCli {

    private final DeleteTaskService deleteTaskService;
    private final Scanner scanner;

    public DeleteTaskCli(DeleteTaskService deleteTaskService, Scanner scanner) {
        this.deleteTaskService = deleteTaskService;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n=== DELETE TASK ===");

        try {
            String id = getTaskId();

            if (!deleteTaskService.taskExists(id)) {
                System.out.println("❌ No task found with ID: " + id);
                return;
            }

            if (!confirmDeletion(id)) {
                System.out.println("🗑️ Deletion cancelled. Task preserved.");
                return;
            }

            Task deletedTask = deleteTaskService.deleteTask(id);

            System.out.println("\n✅ Task deleted successfully!");
            System.out.println("   Removed: " + deletedTask.getText() +
                    " [ID: " + deletedTask.getId() + "]");

        } catch (TaskValidationException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTaskId() {
        System.out.print("Enter task ID to delete: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            throw new TaskValidationException("Task ID cannot be empty");
        }
        return id;
    }

    private boolean confirmDeletion(String id) {
        System.out.println("\n⚠️  WARNING: You are about to delete a task permanently.");
        System.out.print("Are you sure you want to delete task with ID: " + id + "? (y/N): ");

        String confirmation = scanner.nextLine().trim().toLowerCase();

        return confirmation.equals("y") || confirmation.equals("yes");
    }
}
