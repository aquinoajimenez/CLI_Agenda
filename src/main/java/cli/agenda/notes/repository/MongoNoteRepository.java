package cli.agenda.notes.repository;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.model.Note;
import cli.agenda.notes.model.NoteCategory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        for (Document doc : collection.find()) {
            notes.add(fromDocument(doc));
        }
        return notes;
    }

    private Note fromDocument(Document doc) {
        Note note = new Note(
                doc.getString("title"),
                doc.getString("content"),
                null
        );

        note.setId(doc.getObjectId("_id"));

        String category = doc.getString("category");
        if (category != null) {
            note.setCategory(NoteCategory.valueOf(category));
        }
        return note;
    }

    @Override
    public Note findById(String id) {
        Document doc = collection.find(new Document("_id", new ObjectId(id))).first();
        if (doc == null) {
            return null;
        }
        return fromDocument(doc);
    }

    @Override
    public Note update(String id, NoteCreateDTO dto) {
        Document updatedDoc = new Document()
                .append("title", dto.getTitle())
                .append("content", dto.getContent())
                .append("category", dto.getCategory() != null ? dto.getCategory().name() : null)
                .append("updated_at", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        collection.updateOne(
                new Document("_id", new ObjectId(id)),
                new Document("$set", updatedDoc)
        );

        return findById(id);
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(new Document("_id", new ObjectId(id)));
    }
}