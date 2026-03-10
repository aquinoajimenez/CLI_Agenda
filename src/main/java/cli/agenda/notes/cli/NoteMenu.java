package cli.agenda.notes.cli;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
import cli.agenda.notes.model.NoteCategory;
import cli.agenda.notes.service.NoteService;

import java.util.Scanner;

public class NoteMenu {

    private final NoteService noteService;
    private final Scanner scanner;

    public NoteMenu(NoteService noteService, Scanner scanner) {
        this.noteService = noteService;
        this.scanner = scanner;
    }

    public void show() {
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
}