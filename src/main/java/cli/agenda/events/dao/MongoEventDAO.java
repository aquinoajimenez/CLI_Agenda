package cli.agenda.events.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
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
        try{
            Document updateQuery = new Document("$set", updates);
            UpdateResult result = collection.updateOne(Filters.eq("_id", new ObjectId(id)), updateQuery);
            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try{
            DeleteResult delete = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return delete.getDeletedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Optional<Document> findById(String id) {
        try{
            Document findId = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            return Optional.ofNullable(findId);
        }catch(IllegalArgumentException e){
            return Optional.empty();
        }
    }

    @Override
    public List<Document> findUpComing() {
        Date date = new Date();

        return collection.find(Filters.gt("end_date", date)).into(new ArrayList<>());
    }

    @Override
    public List<Document> findPast() {
        Date date = new Date();
        return collection.find(Filters.lt("end_date", date)).into(new ArrayList<>());
    }
}
