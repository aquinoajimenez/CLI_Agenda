package cli.agenda;

import cli.agenda.tasks.cli.CreateTaskCli;
import cli.agenda.tasks.cli.ListPendingTasksCli;
import cli.agenda.tasks.cli.ListCompletedTasksCli;
import cli.agenda.tasks.cli.ListAllTasksCli;
import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.repository.impl.TaskRepositoryImpl;
import cli.agenda.tasks.service.CreateTaskService;
import cli.agenda.tasks.service.ListPendingTasksService;
import cli.agenda.tasks.service.ListCompletedTasksService;
import cli.agenda.tasks.service.ListAllTasksService;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.MongoException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String connectionString = "mongodb://localhost:27017/cli_agenda_db";

            System.out.println("🔄 Connecting to MongoDB: " + connectionString);
            var mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("cli_agenda_db");

            System.out.println("✅ MongoDB connection established!");
            System.out.println("📊 Database: " + database.getName());

            System.out.println("\n🔍 STARTING CONNECTION VERIFICATION...");
            try {
                Document pingResult = database.runCommand(new Document("ping", 1));
                System.out.println("✅ MongoDB ping: " + pingResult.toJson());

                MongoCollection<Document> testColl = database.getCollection("test_connection");
                String testId = "test-" + System.currentTimeMillis();
                Document testDoc = new Document("_id", testId)
                        .append("test", "connection")
                        .append("timestamp", System.currentTimeMillis());

                System.out.println("📝 Attempting to insert test document...");
                testColl.insertOne(testDoc);
                System.out.println("✅ Test document inserted with ID: " + testId);

                Document readTest = testColl.find(new Document("_id", testId)).first();
                if (readTest != null) {
                    System.out.println("✅ Test document read successfully: " + readTest.toJson());
                } else {
                    System.err.println("❌ Could not read test document!");
                }

                try {
                    long taskCount = database.getCollection("tasks").countDocuments();
                    System.out.println("📊 Current tasks in DB: " + taskCount);
                } catch (Exception e) {
                    System.out.println("ℹ️ Collection 'tasks' does not exist yet");
                }

                System.out.println("📚 Available collections:");
                database.listCollectionNames().forEach(coll -> System.out.println("  - " + coll));

                testColl.deleteOne(new Document("_id", testId));
                System.out.println("✅ Cleanup completed (test document deleted)");

            } catch (MongoException e) {
                System.err.println("❌ MongoDB error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("🚨 Application cannot continue without MongoDB connection");
                return;
            } catch (Exception e) {
                System.err.println("❌ Unexpected error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("🚨 Application cannot continue without MongoDB connection");
                return;
            }

            System.out.println("\n✅ ALL VERIFICATIONS SUCCESSFUL!");
            System.out.println("🚀 Starting application...\n");

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
            System.err.println("❌ General error: " + e.getMessage());
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