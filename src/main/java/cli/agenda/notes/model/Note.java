package cli.agenda.notes.model;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;

public class Note {

    private ObjectId id;
    private String title;
    private String content;
    private NoteCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note(String title, String content, NoteCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public NoteCategory getCategory() { return category; }
    public void setCategory(NoteCategory category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}