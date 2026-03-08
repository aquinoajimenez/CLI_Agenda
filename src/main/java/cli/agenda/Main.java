package cli.agenda;

import cli.agenda.infrastructure.repository.tasks.MongoTaskRepository;
import cli.agenda.tasks.cli.CreateTaskCli;
import cli.agenda.tasks.CreateTaskService;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.MongoException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // 🔥 CONNEXIÓ A MONGODB
            String connectionString = "mongodb://localhost:27017/cli_agenda_db";

            System.out.println("🔄 Connectant a MongoDB: " + connectionString);
            var mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("cli_agenda_db");

            System.out.println("✅ Connexió a MongoDB establerta!");
            System.out.println("📊 Base de dades: " + database.getName());

            // 🔍 VERIFICACIÓ DE CONNEXIÓ I PERMISOS D'ESCRIPTURA
            System.out.println("\n🔍 INICIANT VERIFICACIÓ DE CONNEXIÓ...");
            try {
                // Prova 1: Ping a la base de dades
                Document pingResult = database.runCommand(new Document("ping", 1));
                System.out.println("✅ Ping a MongoDB: " + pingResult.toJson());

                // Prova 2: Inserir un document de prova
                MongoCollection<Document> testColl = database.getCollection("test_connection");
                String testId = "test-" + System.currentTimeMillis();
                Document testDoc = new Document("_id", testId)
                        .append("test", "connection")
                        .append("timestamp", System.currentTimeMillis());

                System.out.println("📝 Intentant inserir document de prova...");
                testColl.insertOne(testDoc);
                System.out.println("✅ Document de prova insertat amb ID: " + testId);

                // Prova 3: Llegir el document de prova
                Document readTest = testColl.find(new Document("_id", testId)).first();
                if (readTest != null) {
                    System.out.println("✅ Document de prova llegit correctament: " + readTest.toJson());
                } else {
                    System.err.println("❌ No s'ha pogut llegir el document de prova!");
                }

                // Prova 4: Comptar documents a la col·lecció tasks (si existeix)
                try {
                    long taskCount = database.getCollection("tasks").countDocuments();
                    System.out.println("📊 Tasques actuals a la BD: " + taskCount);
                } catch (Exception e) {
                    System.out.println("ℹ️ La col·lecció 'tasks' encara no existeix");
                }

                // Prova 5: Llistar totes les col·leccions
                System.out.println("📚 Col·leccions disponibles:");
                database.listCollectionNames().forEach(coll -> System.out.println("  - " + coll));

                // Neteja: eliminar el document de prova
                testColl.deleteOne(new Document("_id", testId));
                System.out.println("✅ Neteja completada (document de prova eliminat)");

            } catch (MongoException e) {
                System.err.println("❌ Error de MongoDB: " + e.getMessage());
                e.printStackTrace();
                System.err.println("🚨 L'aplicació no pot continuar sense connexió a MongoDB");
                return;
            } catch (Exception e) {
                System.err.println("❌ Error inesperat: " + e.getMessage());
                e.printStackTrace();
                System.err.println("🚨 L'aplicació no pot continuar sense connexió a MongoDB");
                return;
            }

            System.out.println("\n✅ TOTES LES VERIFICACIONS HAN ESTAT EXITOSES!");
            System.out.println("🚀 Iniciant l'aplicació...\n");

            // Inicialitzar repositori i serveis
            var taskRepository = new MongoTaskRepository(database);
            var createTaskService = new CreateTaskService(taskRepository);
            var createTaskCli = new CreateTaskCli(createTaskService, scanner);

            showMainMenu(scanner, createTaskCli);

        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
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