package cli.agenda.notes.cli;

import cli.agenda.infrastructure.database.DatabaseConnection;
import cli.agenda.notes.repository.MongoNoteRepository;
import cli.agenda.notes.repository.NoteRepository;
import cli.agenda.notes.service.NoteService;
import cli.agenda.notes.service.NoteServiceImpl;
import com.mongodb.client.MongoDatabase;

import java.util.Scanner;

public class NotesApp {

    public static void start() {
        MongoDatabase database = DatabaseConnection.INSTANCE.getDatabase();
        Scanner scanner = new Scanner(System.in);

        NoteRepository noteRepository = new MongoNoteRepository(database);
        NoteService noteService = new NoteServiceImpl(noteRepository);
        NoteMenu noteMenu = new NoteMenu(noteService, scanner);

        noteMenu.show();
    }
}