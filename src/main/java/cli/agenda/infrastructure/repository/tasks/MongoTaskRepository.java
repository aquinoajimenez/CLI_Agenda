package cli.agenda.infrastructure.repository.tasks;

import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoTaskRepository implements TaskRepository {

    private static final String COLLECTION_NAME = "tasks";
    private final MongoCollection<Document> collection;

    public MongoTaskRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
        createIndexes();
    }

    private void createIndexes() {
        collection.createIndex(Indexes.ascending("created_at"));
        collection.createIndex(Indexes.ascending("status"));
    }

    @Override
    public Task save(Task task) {
        Document doc = new Document()
                .append("_id", task.getId())
                .append("text", task.getText())
                .append("due_date", task.getDueDate())
                .append("priority", task.getPriority().name())
                .append("status", task.getStatus().name())
                .append("created_at", task.getCreatedAt());

        collection.insertOne(doc);
        return task;
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
        return new Task.Builder()
                .id(doc.getString("_id"))
                .text(doc.getString("text"))
                .dueDate(doc.getDate("due_date") != null ?
                        doc.getDate("due_date").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() :
                        null)
                .priority(Priority.valueOf(doc.getString("priority")))
                .status(Status.valueOf(doc.getString("status")))
                .createdAt(doc.getDate("created_at").toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}
