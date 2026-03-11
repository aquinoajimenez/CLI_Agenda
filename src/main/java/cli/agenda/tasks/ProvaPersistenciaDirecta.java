package cli.agenda.tasks;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Date;
import java.util.UUID;

public class ProvaPersistenciaDirecta {
    public static void main(String[] args) {
        String connectionString = "mongodb://localhost:27017";
        String dbName = "prova_persistencia";
        String collName = "prova";

        try (var mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collName);

            // 1. Inserir un document
            String id = UUID.randomUUID().toString();
            Document doc = new Document("_id", id)
                    .append("test", "persistència directa")
                    .append("timestamp", new Date());

            collection.insertOne(doc);
            System.out.println("✅ Insertat document amb ID: " + id);

            // 2. Verificar immediatament (mateixa connexió)
            Document verificat = collection.find(new Document("_id", id)).first();
            System.out.println("🔍 Verificat (mateixa connexió): " + (verificat != null ? "TROBAT" : "NO TROBAT"));

            // 3. Esperar i reconnectar per verificar
            System.out.println("⏳ Esperant 2 segons...");
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Nova connexió per verificar persistència
        try (var mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collName);

            // Comprovar que la col·lecció té documents
            long count = collection.countDocuments();
            System.out.println("📊 Documents a la col·lecció '" + collName + "' (nova connexió): " + count);

            if (count > 0) {
                System.out.println("🎉 LA PERSISTÈNCIA FUNCIONA!");
            } else {
                System.err.println("❌ LA PERSISTÈNCIA HA FALLAT!");
            }

            // Neteja (opcional)
            // database.drop();
        }
    }
}