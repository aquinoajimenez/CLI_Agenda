package cli.agenda.tasks.dao.mongodb;

import cli.agenda.tasks.dao.TaskDAO;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MongoDBTaskDAO implements TaskDAO {

    private static final String COLLECTION_NAME = "tasks";
    private final MongoCollection<Document> collection;

    public MongoDBTaskDAO(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
        System.out.println("✅ MongoDB DAO initialized");
    }

    @Override
    public Task insert(Task task) {
        System.out.println("📝 DAO: Inserting task with ID: " + task.getId());

        try {
            Document doc = taskToDocument(task);
            System.out.println("   Document: " + doc.toJson());

            InsertOneResult result = collection
                    .withWriteConcern(WriteConcern.MAJORITY)
                    .insertOne(doc);

            if (result.wasAcknowledged()) {
                System.out.println("✅ DAO: Insert acknowledged by MongoDB");

                Document verified = collection.find(Filters.eq("_id", task.getId())).first();
                if (verified != null) {
                    System.out.println("✅ DAO: Document verified in database");
                    return task;
                } else {
                    System.err.println("❌ DAO: Document not found after insert!");
                    throw new RuntimeException("Document verification failed after insert");
                }
            } else {
                System.err.println("❌ DAO: Insert not acknowledged by MongoDB");
                throw new RuntimeException("Insert not acknowledged by MongoDB");
            }

        } catch (MongoException e) {
            System.err.println("❌ DAO: MongoDB error during insert: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to insert task", e);
        }
    }

    @Override
    public boolean update(Task task) {
        System.out.println("📝 DAO: Updating task with ID: " + task.getId());

        try {
            Bson filter = Filters.eq("_id", task.getId());

            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.set("text", task.getText()));
            updates.add(Updates.set("priority", task.getPriority().name()));
            updates.add(Updates.set("status", task.getStatus().name()));

            if (task.getDueDate() != null) {
                Date dueDate = Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant());
                updates.add(Updates.set("due_date", dueDate));
                System.out.println("   Setting due_date to: " + dueDate);
            } else {
                updates.add(Updates.unset("due_date"));
                System.out.println("   Removing due_date field");
            }

            Bson update = Updates.combine(updates);
            UpdateOptions options = new UpdateOptions().upsert(false);

            UpdateResult result = collection
                    .withWriteConcern(WriteConcern.MAJORITY)
                    .updateOne(filter, update, options);

            if (result.wasAcknowledged()) {
                System.out.println("✅ DAO: Update acknowledged by MongoDB");
                System.out.println("   Matched count: " + result.getMatchedCount());
                System.out.println("   Modified count: " + result.getModifiedCount());

                return true;
            } else {
                System.err.println("❌ DAO: Update not acknowledged by MongoDB");
                return false;
            }

        } catch (MongoException e) {
            System.err.println("❌ DAO: MongoDB error during update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            return Optional.ofNullable(doc).map(this::documentToTask);
        } catch (MongoException e) {
            System.err.println("❌ DAO: Error finding task by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try {
            collection.find().forEach(doc -> tasks.add(documentToTask(doc)));
        } catch (MongoException e) {
            System.err.println("❌ DAO: Error finding all tasks: " + e.getMessage());
        }
        return tasks;
    }

    @Override
    public List<Task> findByStatus(Status status) {
        List<Task> tasks = new ArrayList<>();
        try {
            collection.find(Filters.eq("status", status.name()))
                    .forEach(doc -> tasks.add(documentToTask(doc)));
        } catch (MongoException e) {
            System.err.println("❌ DAO: Error finding tasks by status: " + e.getMessage());
        }
        return tasks;
    }

    @Override
    public boolean deleteById(String id) {
        try {
            DeleteResult result = collection
                    .withWriteConcern(WriteConcern.MAJORITY)
                    .deleteOne(Filters.eq("_id", id));

            if (result.wasAcknowledged()) {
                System.out.println("✅ DAO: Delete acknowledged, deleted: " + result.getDeletedCount());
                return result.getDeletedCount() > 0;
            } else {
                System.err.println("❌ DAO: Delete not acknowledged");
                return false;
            }
        } catch (MongoException e) {
            System.err.println("❌ DAO: Error deleting task: " + e.getMessage());
            return false;
        }
    }

    private Document taskToDocument(Task task) {
        Document doc = new Document("_id", task.getId())
                .append("text", task.getText())
                .append("priority", task.getPriority().name())
                .append("status", task.getStatus().name());

        if (task.getDueDate() != null) {
            doc.append("due_date", Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
        }

        doc.append("created_at", Date.from(task.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));

        return doc;
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