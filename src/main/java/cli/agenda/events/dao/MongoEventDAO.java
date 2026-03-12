package cli.agenda.events.dao;

import com.mongodb.client.MongoCollection;

import org.bson.Document;

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
}
