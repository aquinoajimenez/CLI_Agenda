package cli.agenda.notes.service;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.mapper.NoteMapper;
import cli.agenda.notes.model.Note;
import cli.agenda.notes.repository.NoteRepository;

public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public NoteResponseDTO createNote(NoteCreateDTO dto) {
        Note note = NoteMapper.toNote(dto);
        Note saved = noteRepository.save(note);
        return NoteMapper.toResponseDTO(saved);
    }
}