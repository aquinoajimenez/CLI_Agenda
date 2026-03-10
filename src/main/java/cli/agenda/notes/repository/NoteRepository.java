package cli.agenda.notes.repository;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.model.Note;

import java.util.List;

public interface NoteRepository {
    Note save(Note note);

    List<Note> findAll();

    Note update(String id, NoteCreateDTO dto);

    Note findById(String id);

}
