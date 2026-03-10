package cli.agenda.notes.dto;

import cli.agenda.notes.model.NoteCategory;
import java.time.LocalDateTime;

public class NoteResponseDTO {

    private String id;
    private String title;
    private String content;
    private NoteCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoteResponseDTO(String id, String title, String content,
                           NoteCategory category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public NoteCategory getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}