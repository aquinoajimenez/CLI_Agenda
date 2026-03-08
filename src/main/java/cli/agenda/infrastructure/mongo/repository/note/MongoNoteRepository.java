package cli.agenda.infrastructure.mongo.repository.note;

import cli.agenda.notes.model.Note;
import cli.agenda.notes.repository.NoteRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoNoteRepository implements NoteRepository {

    private final MongoCollection<Document> collection;

    public MongoNoteRepository(MongoDatabase database) {
        this.collection = database.getCollection("notes");
    }

    @Override
    public Note save(Note note) {
        Document doc = new Document()
                .append("title", note.getTitle())
                .append("content", note.getContent());

        collection.insertOne(doc);
        note.setId(doc.getObjectId("_id"));
        return note;
    }
}