package cli.agenda.tasks.notes;

public class Note {
    private Object id;
    private String title;
    private String content;

public Note(String title, String content) {
        this.title = title;
        this.content = content;
}

public Object getId() {
        return id;
    }

public void setId(Object id) {
        this.id = id;
    }

public String getTitle() {
        return title;
    }

public void setTitle(String title) {
        this.title = title;
    }

public String getContent() {
        return content;
    }

public void setContent(String content) {
        this.content = content;
    }
}
