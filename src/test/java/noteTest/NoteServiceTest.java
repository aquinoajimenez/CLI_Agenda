package noteTest;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.model.Note;
import cli.agenda.notes.model.NoteCategory;
import cli.agenda.notes.repository.NoteRepository;
import cli.agenda.notes.service.NoteService;
import cli.agenda.notes.service.NoteServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoteServiceTest {

    private NoteService noteService;
    private FakeNoteRepository fakeRepository;

    @BeforeEach
    void setUp() {
        fakeRepository = new FakeNoteRepository();
        noteService = new NoteServiceImpl(fakeRepository);
    }

    // CREATE
    @Test
    void createNote_shouldReturnNoteWithCorrectTitle() {
        NoteCreateDTO dto = new NoteCreateDTO("Title", "Content", NoteCategory.TRABAJO);

        NoteResponseDTO result = noteService.createNote(dto);

        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void createNote_shouldReturnNoteWithNullCategory_whenNoCategoryProvided() {
        NoteCreateDTO dto = new NoteCreateDTO("Title", "Content", null);

        NoteResponseDTO result = noteService.createNote(dto);

        assertThat(result.getCategory()).isNull();
    }

    // FIND ALL
    @Test
    void findAll_shouldReturnAllNotes() {
        noteService.createNote(new NoteCreateDTO("Note 1", "Content 1", null));
        noteService.createNote(new NoteCreateDTO("Note 2", "Content 2", null));

        assertThat(noteService.findAll()).hasSize(2);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoNotes() {
        assertThat(noteService.findAll()).isEmpty();
    }

    // FIND BY ID
    @Test
    void findById_shouldReturnNote_whenNoteExists() {
        NoteResponseDTO created = noteService.createNote(new NoteCreateDTO("Title", "Content", null));

        NoteResponseDTO result = noteService.findById(created.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void findById_shouldReturnNull_whenNoteDoesNotExist() {
        NoteResponseDTO result = noteService.findById("000000000000000000000000");

        assertThat(result).isNull();
    }

    // UPDATE
    @Test
    void update_shouldReturnUpdatedNote() {
        NoteResponseDTO created = noteService.createNote(new NoteCreateDTO("Old Title", "Content", null));
        NoteCreateDTO updateDto = new NoteCreateDTO("New Title", "Content", null);

        NoteResponseDTO result = noteService.update(created.getId(), updateDto);

        assertThat(result.getTitle()).isEqualTo("New Title");
    }

    @Test
    void update_shouldReturnNull_whenNoteDoesNotExist() {
        NoteCreateDTO updateDto = new NoteCreateDTO("New Title", "Content", null);

        NoteResponseDTO result = noteService.update("000000000000000000000000", updateDto);

        assertThat(result).isNull();
    }

    // DELETE
    @Test
    void delete_shouldRemoveNoteFromRepository() {
        NoteCreateDTO dto = new NoteCreateDTO("Test", "Content", NoteCategory.TRABAJO);
        NoteResponseDTO created = noteService.createNote(dto);

        noteService.delete(created.getId());

        assertThat(noteService.findAll()).isEmpty();
    }

    @Test
    void delete_shouldDoNothing_whenNoteDoesNotExist() {
        noteService.delete("000000000000000000000000");

        assertThat(noteService.findAll()).isEmpty();
    }

    static class FakeNoteRepository implements NoteRepository {

        private final List<Note> notes = new ArrayList<>();

        @Override
        public Note save(Note note) {
            note.setId(new ObjectId());
            notes.add(note);
            return note;
        }

        @Override
        public List<Note> findAll() {
            return notes;
        }

        @Override
        public Note findById(String id) {
            return notes.stream()
                    .filter(n -> n.getId().toHexString().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Note update(String id, NoteCreateDTO dto) {
            Note note = findById(id);
            if (note == null) return null;
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            note.setCategory(dto.getCategory());
            return note;
        }

        @Override
        public void delete(String id) {
            notes.removeIf(n -> n.getId().toHexString().equals(id));
        }
    }
}