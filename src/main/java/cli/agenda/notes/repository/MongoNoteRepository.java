package cli.agenda.notes.repository;

import cli.agenda.notes.model.Note;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.ZoneId;
import java.util.Date;

public class MongoNoteRepository implements NoteRepository {

    private final MongoCollection<Document> collection;

    public MongoNoteRepository(MongoDatabase database) {
        this.collection = database.getCollection("notes");
    }

    @Override
    public Note save(Note note) {
        Document doc = new Document()
                .append("title", note.getTitle())
                .append("content", note.getContent())
                .append("category", note.getCategory() != null ? note.getCategory().name() : null)
                .append("created_at", Date.from(note.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()))
                .append("updated_at", null);

        collection.insertOne(doc);
        note.setId(doc.getObjectId("_id"));
        return note;
    }
}