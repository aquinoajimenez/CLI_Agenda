package cli.agenda;

import cli.agenda.infrastructure.repository.tasks.MongoTaskRepository;
import cli.agenda.tasks.cli.CreateTaskCli;
import cli.agenda.tasks.service.CreateTaskService;
import cli.agenda.infrastructure.database.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            var mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("agenda_db");

            var taskRepository = new MongoTaskRepository(database);

            var createTaskService = new CreateTaskService(taskRepository);

            var createTaskCli = new CreateTaskCli(createTaskService, scanner);

            showMainMenu(scanner, createTaskCli);
        }
    }

    private static void showMainMenu(Scanner scanner, CreateTaskCli createTaskCli) {
        while (true) {
            System.out.println("\n=== AGENDA CLI ===");
            System.out.println("1. Create Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createTaskCli.start();
                    break;
                case "2":
                    System.out.println("List tasks - Coming soon!");
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}