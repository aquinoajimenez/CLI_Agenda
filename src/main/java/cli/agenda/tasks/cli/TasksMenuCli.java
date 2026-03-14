package cli.agenda.tasks.cli;

import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.repository.impl.TaskRepositoryImpl;
import cli.agenda.tasks.service.*;
import com.mongodb.client.MongoClients;

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

    public TasksMenuCli(Scanner scanner) {
        this.scanner = scanner;

        System.out.println("📦 Initializing Tasks module...");
        var mongoClient = MongoClients.create("mongodb://localhost:27017");
        var database = mongoClient.getDatabase("cli_agenda_db");

        var taskDAO = new MongoDBTaskDAO(database);
        var taskRepository = new TaskRepositoryImpl(taskDAO);

        var createTaskService = new CreateTaskService(taskRepository);
        var listPendingTasksService = new ListPendingTasksService(taskRepository);
        var listCompletedTasksService = new ListCompletedTasksService(taskRepository);
        var listAllTasksService = new ListAllTasksService(taskRepository);
        var updateTaskService = new UpdateTaskService(taskRepository);
        var deleteTaskService = new DeleteTaskService(taskRepository);
        var completeTaskService = new CompleteTaskService(taskRepository);

        this.createTaskCli = new CreateTaskCli(createTaskService, scanner);
        this.listPendingTasksCli = new ListPendingTasksCli(listPendingTasksService);
        this.listCompletedTasksCli = new ListCompletedTasksCli(listCompletedTasksService);
        this.listAllTasksCli = new ListAllTasksCli(listAllTasksService);
        this.updateTaskCli = new UpdateTaskCli(updateTaskService, scanner);
        this.deleteTaskCli = new DeleteTaskCli(deleteTaskService, scanner);
        this.completeTaskCli = new CompleteTaskCli(completeTaskService, scanner);

        System.out.println("✅ Tasks module initialized successfully");
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
        System.out.println("\n=== 📋 TASKS MENU 📋 ===");
        System.out.println("1. ➕ Create task");
        System.out.println("2. 📌 List PENDING tasks");
        System.out.println("3. ✅ List COMPLETED tasks");
        System.out.println("4. 📚 List ALL tasks");
        System.out.println("5. ✏️ Update task");
        System.out.println("6. ✅ Mark task as COMPLETED");
        System.out.println("7. 🗑️ Delete task");
        System.out.println("8. 🚪 Back to main menu");
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
                System.out.println("🔙 Returning to main menu...");
                return false;
            default:
                System.out.println("❌ Invalid option. Choose 1-8.");
                return true;
        }
    }
}
