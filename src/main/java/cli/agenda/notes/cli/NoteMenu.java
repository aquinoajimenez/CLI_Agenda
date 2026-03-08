package cli.agenda.notes.cli;

import cli.agenda.notes.dto.NoteCreateDTO;
import cli.agenda.notes.dto.NoteResponseDTO;
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

        System.out.print("Content: ");
        String content = scanner.nextLine().trim();

        NoteResponseDTO response = noteService.createNote(new NoteCreateDTO(title, content));

        System.out.println("Note created! ID: " + response.getId());
        System.out.println("Title: " + response.getTitle());
        System.out.println("Content: " + response.getContent());
    }
}