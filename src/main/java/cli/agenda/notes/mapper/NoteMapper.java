package cli.agenda.notes.mapper;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.model.Note;

public class NoteMapper {

    private NoteMapper() {}

    public static Note toNote(NoteCreateDTO dto) {
        return new Note(dto.getTitle(), dto.getContent());
    }

    public static NoteResponseDTO toResponseDTO(Note note) {
        return new NoteResponseDTO(
                note.getId().toString(),
                note.getTitle(),
                note.getContent()
        );
    }
}
