package cli.agenda.notes.service;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.mapper.NoteMapper;
import cli.agenda.notes.model.Note;
import cli.agenda.notes.repository.NoteRepository;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<NoteResponseDTO> findAll() {
        List<Note> notes = noteRepository.findAll();
        List<NoteResponseDTO> result = new ArrayList<>();
        for (Note note : notes) {
            result.add(NoteMapper.toResponseDTO(note));
        }
        return result;
    }

    @Override
    public NoteResponseDTO update(String id, NoteCreateDTO dto) {
        Note updated = noteRepository.update(id, dto);
        return NoteMapper.toResponseDTO(updated);
    }

    @Override
    public NoteResponseDTO findById(String id) {
        Note note = noteRepository.findById(id);
        if (note == null) {
            return null;
        }
        return NoteMapper.toResponseDTO(note);
    }
}