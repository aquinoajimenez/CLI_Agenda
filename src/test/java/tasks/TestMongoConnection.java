package tasks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class TestMongoConnection {
    public static void main(String[] args) {
        String uri = "mongodb://root:rootpassword@localhost:27017/cli_agenda_db?authSource=admin";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase adminDb = mongoClient.getDatabase("admin");
            adminDb.runCommand(new Document("ping", 1));
            System.out.println("Connection successfully established!");

            MongoDatabase database = mongoClient.getDatabase("cli_agenda_db");
            MongoCollection<Document> collection = database.getCollection("tasks");

            Document firstTask = collection.find().first();
            if (firstTask != null) {
                System.out.println("\nFirst task found:");
                System.out.println("Title: " + firstTask.getString("text"));
                System.out.println("Priority: " + firstTask.getString("priority"));
                System.out.println("Status: " + firstTask.getString("status"));
                System.out.println("Due date: " + firstTask.getDate("due_date"));
            } else {
                System.out.println("No tasks found.");
            }

            long count = collection.countDocuments();
            System.out.println("\nTotal tasks: " + count);

        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}