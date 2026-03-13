package cli.agenda.events.dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;
import java.util.Optional;

public class MongoEventDAO implements EventDao{
    private final MongoCollection<Document> collection;

    public MongoEventDAO(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public String save(Document document) {
        collection.insertOne(document);
        return document.getObjectId("_id").toHexString();
    }

    @Override
    public boolean update(String id, Document updates) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public Optional<Document> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Document> findUpComing() {
        return List.of();
    }

    @Override
    public List<Document> findPast() {
        return List.of();
    }
}
