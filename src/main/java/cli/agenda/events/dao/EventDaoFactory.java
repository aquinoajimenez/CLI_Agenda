package cli.agenda.events.dao;

import cli.agenda.infrastructure.database.DatabaseConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class EventDaoFactory {

    public static EventDao createMongoEventDao(){
        MongoCollection<Document> collection = DatabaseConnection.INSTANCE.getDatabase().getCollection("events");
        return new MongoEventDAO(collection);
    }
}
