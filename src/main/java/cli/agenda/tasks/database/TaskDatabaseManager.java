package cli.agenda.tasks.database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.MongoException;

public class TaskDatabaseManager {

    private final MongoDatabase database;
    private final String connectionString;

    public TaskDatabaseManager(String connectionString) {
        this.connectionString = connectionString;
        this.database = initializeDatabase();
    }

    private MongoDatabase initializeDatabase() {
        System.out.println("🔄 Connecting to MongoDB: " + connectionString);
        var mongoClient = MongoClients.create(connectionString);
        MongoDatabase db = mongoClient.getDatabase("cli_agenda_db");
        System.out.println("✅ MongoDB connection established!");
        System.out.println("📊 Database: " + db.getName());
        return db;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public boolean verifyConnection() {
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
                return false;
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

            return true;

        } catch (MongoException e) {
            System.err.println("❌ MongoDB error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}