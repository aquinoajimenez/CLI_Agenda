package cli.agenda.notes.service;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;

import java.util.List;

public interface NoteService {

    NoteResponseDTO createNote(NoteCreateDTO dto);

    List<NoteResponseDTO> findAll();
}
