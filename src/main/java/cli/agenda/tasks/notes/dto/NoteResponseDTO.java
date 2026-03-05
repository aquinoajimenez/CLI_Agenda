package cli.agenda.tasks.notes.dto;

public class NoteResponseDTO {

    private String id;
    private String title;
    private String content;

    public NoteResponseDTO(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
