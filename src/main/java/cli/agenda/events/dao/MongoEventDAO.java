package cli.agenda.events.dao;

import com.mongodb.client.MongoCollection;
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
<<<<<<< HEAD
=======
import org.bson.Document;
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
        try{
            Document updateQuery = new Document("$set", updates);
            UpdateResult result = collection.updateOne(Filters.eq("_id", new ObjectId(id)), updateQuery);
            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
<<<<<<< HEAD
=======
        return false;
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
    }

    @Override
    public boolean delete(String id) {
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
        try{
            DeleteResult delete = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return delete.getDeletedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
<<<<<<< HEAD
=======
        return false;
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
    }

    @Override
    public Optional<Document> findById(String id) {
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
        try{
            Document findId = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            return Optional.ofNullable(findId);
        }catch(IllegalArgumentException e){
            return Optional.empty();
        }
<<<<<<< HEAD
=======
        return Optional.empty();
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
    }

    @Override
    public List<Document> findUpComing() {
<<<<<<< HEAD
<<<<<<< HEAD
        Date date = new Date();

        return collection.find(Filters.gt("end_date", date)).into(new ArrayList<>());
=======
        return List.of();
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
        Date date = new Date();

        return collection.find(Filters.gt("end_date", date)).into(new ArrayList<>());
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
    }

    @Override
    public List<Document> findPast() {
<<<<<<< HEAD
<<<<<<< HEAD
        Date date = new Date();
        return collection.find(Filters.lt("end_date", date)).into(new ArrayList<>());
=======
        return List.of();
>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
=======
        Date date = new Date();
        return collection.find(Filters.lt("end_date", date)).into(new ArrayList<>());
>>>>>>> 7b98e72 (feat: dao completed using factory pattern)
    }
}
