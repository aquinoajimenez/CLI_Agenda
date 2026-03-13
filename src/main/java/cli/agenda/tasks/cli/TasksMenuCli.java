package cli.agenda.tasks.cli;

import java.util.Scanner;

public class TasksMenuCli {

    private final Scanner scanner;
    private final CreateTaskCli createTaskCli;
    private final ListPendingTasksCli listPendingTasksCli;
    private final ListCompletedTasksCli listCompletedTasksCli;
    private final ListAllTasksCli listAllTasksCli;
    private final UpdateTaskCli updateTaskCli;
    private final DeleteTaskCli deleteTaskCli;
    private final CompleteTaskCli completeTaskCli;

    public TasksMenuCli(Scanner scanner,
                        CreateTaskCli createTaskCli,
                        ListPendingTasksCli listPendingTasksCli,
                        ListCompletedTasksCli listCompletedTasksCli,
                        ListAllTasksCli listAllTasksCli,
                        UpdateTaskCli updateTaskCli,
                        DeleteTaskCli deleteTaskCli,
                        CompleteTaskCli completeTaskCli) {
        this.scanner = scanner;
        this.createTaskCli = createTaskCli;
        this.listPendingTasksCli = listPendingTasksCli;
        this.listCompletedTasksCli = listCompletedTasksCli;
        this.listAllTasksCli = listAllTasksCli;
        this.updateTaskCli = updateTaskCli;
        this.deleteTaskCli = deleteTaskCli;
        this.completeTaskCli = completeTaskCli;
    }

    public void start() {
        while (true) {
            displayMenu();

            String choice = scanner.nextLine().trim();

            if (!processChoice(choice)) {
                break;
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== 📋 AGENDA CLI 📋 ===");
        System.out.println("1. ➕ Create task");
        System.out.println("2. 📌 List PENDING tasks");
        System.out.println("3. ✅ List COMPLETED tasks");
        System.out.println("4. 📚 List ALL tasks");
        System.out.println("5. ✏️ Update task");
        System.out.println("6. ✅ Mark task as COMPLETED");
        System.out.println("7. 🗑️ Delete task");
        System.out.println("8. 🚪 Exit");
        System.out.print("Choose an option: ");
    }

    private boolean processChoice(String choice) {
        switch (choice) {
            case "1":
                createTaskCli.start();
                return true;
            case "2":
                listPendingTasksCli.start();
                return true;
            case "3":
                listCompletedTasksCli.start();
                return true;
            case "4":
                listAllTasksCli.start();
                return true;
            case "5":
                updateTaskCli.start();
                return true;
            case "6":
                completeTaskCli.start();
                return true;
            case "7":
                deleteTaskCli.start();
                return true;
            case "8":
                System.out.println("👋 Goodbye!");
                return false;
            default:
                System.out.println("❌ Invalid option. Choose 1-8.");
                return true;
        }
    }
}
