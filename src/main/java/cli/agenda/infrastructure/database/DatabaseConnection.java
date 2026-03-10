package cli.agenda.infrastructure.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public enum DatabaseConnection {
    INSTANCE;

    private MongoClient client;
    private MongoDatabase database;

    DatabaseConnection() {
        try {
            String uri = "mongodb://localhost:27017";
            System.out.println("🔄 Connectant a MongoDB: " + uri);

            this.client = MongoClients.create(uri);
            this.database = this.client.getDatabase("cli_agenda_db");

            database.runCommand(new Document("ping", 1));
            System.out.println("✅ Connexió a MongoDB establerta amb èxit.");
            System.out.println("📊 Base de dades: " + database.getName());

        } catch (MongoException e) {
            System.err.println("❌ Error de MongoDB: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error en URI: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}