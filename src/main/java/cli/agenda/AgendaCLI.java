package cli.agenda;

import cli.agenda.events.dao.EventDaoFactory;
import cli.agenda.events.repository.EventRepositoryImpl;
import cli.agenda.events.service.EventServiceImpl;
import cli.agenda.tasks.cli.TasksMenuCli;
// import cli.agenda.notes.cli.NotesMenuCli;
import cli.agenda.events.ui.EventsMenuCli;

import java.util.Scanner;

public class AgendaCLI {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            TasksMenuCli tasksMenu = new TasksMenuCli(scanner);
            // NotesMenuCli notesMenu = new NotesMenuCli(scanner);
            EventsMenuCli eventsMenu = new EventsMenuCli(new EventServiceImpl(new EventRepositoryImpl(EventDaoFactory.createMongoEventDao())),scanner);

            while (true) {
                displayMainMenu();

                String choice = scanner.nextLine().trim();

                if (!processMainChoice(choice, tasksMenu, eventsMenu,scanner)) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=================================");
        System.out.println("   📋 PERSONAL CLI AGENDA 📋   ");
        System.out.println("=================================");
        System.out.println("1. 📌 Access to your TASKS");
        System.out.println("2. 📝 Access to your NOTES");
        System.out.println("3. 📅 Access to your EVENTS");
        System.out.println("4. 🚪 Exit");
        System.out.print("Choose an option: ");
    }

    private static boolean processMainChoice(String choice,
                                             TasksMenuCli tasksMenu,
                                             EventsMenuCli eventsMenu,
                                             Scanner scanner) {
        switch (choice) {
            case "1":
                System.out.println("\n🔜 Redirecting to TASKS menu...");
                tasksMenu.start();
                return true;
            case "2":
                System.out.println("\n🔜 NOTES menu coming soon...");
                return true;
            case "3":
                System.out.println("\n🔜 Redirecting to EVENTS menu...");
                eventsMenu.start();
                return true;
            case "4":
                System.out.println("\n👋 Thank you for using Personal CLI Agenda. Goodbye!");
                return false;
            default:
                System.out.println("❌ Invalid option. Please choose 1-4.");
                return true;
        }
    }
}
