package cli.agenda.infrastructure.repository.tasks;

import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MongoTaskRepository implements TaskRepository {

    private static final String COLLECTION_NAME = "tasks";
    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    public MongoTaskRepository(MongoDatabase database) {
        this.database = database;
        this.collection = database.getCollection(COLLECTION_NAME);
        System.out.println("✅ Repositori inicialitzat: " + collection.getNamespace());
    }

    @Override
    public Task save(Task task) {
        System.out.println("\n=== DESAT AMB MÈTODE SIMPLIFICAT ===");

        try {
            Date dueDate = task.getDueDate() != null
                    ? Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant())
                    : null;
            Date createdAt = Date.from(task.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());

            Document doc = new Document()
                    .append("_id", task.getId())
                    .append("text", task.getText())
                    .append("due_date", dueDate)
                    .append("priority", task.getPriority().name())
                    .append("status", task.getStatus().name())
                    .append("created_at", createdAt);

            System.out.println("Insertant: " + doc.toJson());

            collection.insertOne(doc);

            Document verificat = collection.find(Filters.eq("_id", task.getId())).first();
            if (verificat != null) {
                System.out.println("✅ ÈXIT (mateixa connexió): Document verificat a BD");
                System.out.println("Verificat: " + verificat.toJson());
            } else {
                System.err.println("❌ ERROR (mateixa connexió): No es troba després d'insertar!");
            }

            System.out.println("\n=== VERIFICACIÓ AMB NOVA CONNEXIÓ ===");
            try {
                MongoClient newClient = MongoClients.create("mongodb://localhost:27017");
                MongoDatabase newDb = newClient.getDatabase("cli_agenda_db");
                MongoCollection<Document> newColl = newDb.getCollection("tasks");

                System.out.println("Esperant 2 segons per propagació...");
                Thread.sleep(2000);

                Document found = newColl.find(Filters.eq("_id", task.getId())).first();
                if (found != null) {
                    System.out.println("✅ VERIFICAT AMB NOVA CONNEXIÓ: Document trobat!");
                    System.out.println("Document: " + found.toJson());
                } else {
                    System.err.println("❌ ERROR AMB NOVA CONNEXIÓ: Document NO trobat!");

                    System.out.println("Documents a la BD (segons nova connexió):");
                    newColl.find().forEach(d ->
                            System.out.println("  - " + d.getString("_id") + ": " + d.getString("text")));
                }
                newClient.close();

            } catch (Exception e) {
                System.err.println("Error en verificació addicional: " + e.getMessage());
                e.printStackTrace();
            }

            return task;

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error guardant tasca", e);
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(doc).map(this::documentToTask);
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        collection.find().forEach(doc -> tasks.add(documentToTask(doc)));
        return tasks;
    }

    @Override
    public boolean deleteById(String id) {
        return collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
    }

    private Task documentToTask(Document doc) {
        Date dueDate = doc.getDate("due_date");
        Date createdAt = doc.getDate("created_at");

        return new Task.Builder()
                .id(doc.getString("_id"))
                .text(doc.getString("text"))
                .dueDate(dueDate != null
                        ? dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .priority(Priority.valueOf(doc.getString("priority")))
                .status(Status.valueOf(doc.getString("status")))
                .createdAt(createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}