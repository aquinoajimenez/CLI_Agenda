package cli.agenda.tasks.notes.repository;

import cli.agenda.tasks.notes.model.Note;

public interface NoteRepository {
    Note save(Note note);
}
