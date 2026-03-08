package cli.agenda.notes.repository;

import cli.agenda.notes.model.Note;

public interface NoteRepository {
    Note save(Note note);
}
