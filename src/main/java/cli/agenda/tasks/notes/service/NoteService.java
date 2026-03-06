package cli.agenda.tasks.notes.service;

import cli.agenda.tasks.notes.dto.NoteCreateDTO;
import cli.agenda.tasks.notes.dto.NoteResponseDTO;

public interface NoteService {

    NoteResponseDTO createNote(NoteCreateDTO dto);
}
