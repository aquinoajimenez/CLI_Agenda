package cli.agenda.notes.cli;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.model.NoteCategory;
import cli.agenda.notes.service.NoteService;

import java.util.List;
import java.util.Scanner;

public class NoteMenu {

    private final NoteService noteService;
    private final Scanner scanner;

    public NoteMenu(NoteService noteService, Scanner scanner) {
        this.noteService = noteService;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n===== NOTES MENU =====");
        System.out.println("1. Create note");
        System.out.println("2. List all notes");
        System.out.println("3. Find note by ID");
        System.out.println("4. Update note");
        System.out.println("0. Exit");
        System.out.print("Select: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1" -> createNote();
            case "2" -> listAllNotes();
            case "3" -> findNoteById();
            case "4" -> updateNote();
            case "0" -> System.out.println("Goodbye!");
            default -> System.out.println("Invalid option.");
        }
    }

    private void createNote() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Content (optional): ");
        String content = scanner.nextLine().trim();

        System.out.println("Category (optional): ");
        System.out.println("1. TRABAJO");
        System.out.println("2. UNIVERSIDAD");
        System.out.println("3. SOCIAL");
        System.out.println("0. None");
        System.out.print("Select: ");
        String option = scanner.nextLine().trim();

        NoteCategory category = switch (option) {
            case "1" -> NoteCategory.TRABAJO;
            case "2" -> NoteCategory.UNIVERSIDAD;
            case "3" -> NoteCategory.SOCIAL;
            default -> null;
        };

        NoteResponseDTO response = noteService.createNote(new NoteCreateDTO(title, content, category));

        System.out.println("Note created! ID: " + response.getId());
        System.out.println("Title: " + response.getTitle());
        System.out.println("Content: " + response.getContent());
        System.out.println("Category: " + response.getCategory());
        System.out.println("Created at: " + response.getCreatedAt());
    }

    private void listAllNotes() {
        List<NoteResponseDTO> notes = noteService.findAll();
        if (notes.isEmpty()) {
            System.out.println("No notes found.");
            return;
        }
        System.out.println("\n--- All Notes ---");
        for (NoteResponseDTO note : notes) {
            System.out.println("ID: " + note.getId());
            System.out.println("Title: " + note.getTitle());
            System.out.println("Content: " + note.getContent());
            System.out.println("Category: " + note.getCategory());
            System.out.println("-------------------------");
        }
    }

    private void findNoteById() {
        System.out.print("Enter note ID: ");
        String id = scanner.nextLine().trim();
        NoteResponseDTO note = noteService.findById(id);
        if (note == null) {
            System.out.println("Note not found.");
            return;
        }
        System.out.println("ID: " + note.getId());
        System.out.println("Title: " + note.getTitle());
        System.out.println("Content: " + note.getContent());
        System.out.println("Category: " + note.getCategory());
    }

    private void updateNote() {
        System.out.print("Enter note ID to update: ");
        String id = scanner.nextLine().trim();

        System.out.print("New title: ");
        String title = scanner.nextLine().trim();

        System.out.print("New content (optional): ");
        String content = scanner.nextLine().trim();

        System.out.println("New category (optional): ");
        System.out.println("1. TRABAJO");
        System.out.println("2. UNIVERSIDAD");
        System.out.println("3. SOCIAL");
        System.out.println("0. None");
        System.out.print("Select: ");
        String option = scanner.nextLine().trim();

        NoteCategory category = switch (option) {
            case "1" -> NoteCategory.TRABAJO;
            case "2" -> NoteCategory.UNIVERSIDAD;
            case "3" -> NoteCategory.SOCIAL;
            default -> null;
        };

        NoteResponseDTO response = noteService.update(id, new NoteCreateDTO(title, content, category));
        System.out.println("Note updated!");
        System.out.println("Title: " + response.getTitle());
        System.out.println("Content: " + response.getContent());
        System.out.println("Category: " + response.getCategory());
    }
}