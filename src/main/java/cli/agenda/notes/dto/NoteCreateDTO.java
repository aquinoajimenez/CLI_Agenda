package cli.agenda.notes.dto;

import cli.agenda.notes.model.NoteCategory;

public class NoteCreateDTO {

    private String title;
    private String content;
    private NoteCategory category;

    public NoteCreateDTO(String title, String content, NoteCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public NoteCategory getCategory() { return category; }
    public void setCategory(NoteCategory category) { this.category = category; }
}