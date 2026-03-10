package cli.agenda.notes.repository;

import cli.agenda.notes.model.Note;

import java.util.List;

public interface NoteRepository {
    Note save(Note note);

    List<Note> findAll();
}
