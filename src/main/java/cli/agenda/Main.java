package cli.agenda;

import cli.agenda.infrastructure.database.DatabaseConnection;
import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.repository.MongoTaskRepository;
import cli.agenda.tasks.cli.CreateTaskCli;
import cli.agenda.tasks.cli.ListPendingTasksCli;
import cli.agenda.tasks.cli.ListCompletedTasksCli;
import cli.agenda.tasks.cli.ListAllTasksCli;
import cli.agenda.tasks.repository.impl.TaskRepositoryImpl;
import cli.agenda.tasks.service.CreateTaskService;
import cli.agenda.tasks.service.ListPendingTasksService;
import cli.agenda.tasks.service.ListCompletedTasksService;
import cli.agenda.tasks.service.ListAllTasksService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            var database = DatabaseConnection.INSTANCE.getDatabase();

            if (database == null) {
                System.err.println("❌ No es pot continuar sense connexió a MongoDB");
                return;
            }

            var taskDAO = new MongoDBTaskDAO(database);
            var taskRepository = new TaskRepositoryImpl(taskDAO);

            var createTaskService = new CreateTaskService(taskRepository);
            var createTaskCli = new CreateTaskCli(createTaskService, scanner);

            var listPendingTasksService = new ListPendingTasksService(taskRepository);
            var listPendingTasksCli = new ListPendingTasksCli(listPendingTasksService);

            var listCompletedTasksService = new ListCompletedTasksService(taskRepository);
            var listCompletedTasksCli = new ListCompletedTasksCli(listCompletedTasksService);

            var listAllTasksService = new ListAllTasksService(taskRepository);
            var listAllTasksCli = new ListAllTasksCli(listAllTasksService);

            showMainMenu(scanner,
                    createTaskCli,
                    listPendingTasksCli,
                    listCompletedTasksCli,
                    listAllTasksCli);

        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMainMenu(Scanner scanner,
                                     CreateTaskCli createTaskCli,
                                     ListPendingTasksCli listPendingTasksCli,
                                     ListCompletedTasksCli listCompletedTasksCli,
                                     ListAllTasksCli listAllTasksCli) {
        while (true) {
            System.out.println("\n=== 📋 AGENDA CLI 📋 ===");
            System.out.println("1. ➕ Create task");
            System.out.println("2. 📌 List PENDING tasks");
            System.out.println("3. ✅ List COMPLETED tasks");
            System.out.println("4. 📚 List ALL tasks");
            System.out.println("5. 🚪 Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createTaskCli.start();
                    break;
                case "2":
                    listPendingTasksCli.start();
                    break;
                case "3":
                    listCompletedTasksCli.start();
                    break;
                case "4":
                    listAllTasksCli.start();
                    break;
                case "5":
                    System.out.println("👋 Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid option. Choose 1-5.");
            }
        }
    }
}