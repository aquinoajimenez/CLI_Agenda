package cli.agenda.tasks.notes.model;

import org.bson.types.ObjectId;

public class Note {
    private ObjectId id;
    private String title;
    private String content;

public Note(String title, String content) {
        this.title = title;
        this.content = content;
}

public Object getId() {
        return id;
    }

public void setId(ObjectId id) {
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
