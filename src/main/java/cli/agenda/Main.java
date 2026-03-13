package cli.agenda;

import cli.agenda.tasks.cli.TasksMenuCli;
import cli.agenda.tasks.database.TaskDatabaseManager;
import cli.agenda.tasks.factory.TaskServiceFactory;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            TaskDatabaseManager dbManager = new TaskDatabaseManager("mongodb://localhost:27017/cli_agenda_db");

            if (!dbManager.verifyConnection()) {
                System.err.println("🚨 Application cannot continue without MongoDB connection");
                return;
            }

            TaskServiceFactory factory = new TaskServiceFactory(dbManager, scanner);

            TasksMenuCli menu = new TasksMenuCli(
                    scanner,
                    factory.getCreateTaskCli(),
                    factory.getListPendingTasksCli(),
                    factory.getListCompletedTasksCli(),
                    factory.getListAllTasksCli(),
                    factory.getUpdateTaskCli(),
                    factory.getDeleteTaskCli(),
                    factory.getCompleteTaskCli()
            );

            menu.start();

        } catch (Exception e) {
            System.err.println("❌ General error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}