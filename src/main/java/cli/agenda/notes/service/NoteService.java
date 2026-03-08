package cli.agenda.notes.service;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;

public interface NoteService {

    NoteResponseDTO createNote(NoteCreateDTO dto);
}
