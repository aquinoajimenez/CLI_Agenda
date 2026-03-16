package cli.agenda.tasks.factory;

import cli.agenda.tasks.cli.*;
import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.database.TaskDatabaseManager;
import cli.agenda.tasks.repository.impl.TaskRepositoryImpl;
import cli.agenda.tasks.service.*;
import com.mongodb.client.MongoDatabase;

import java.util.Scanner;

public class TaskServiceFactory {

    private final MongoDatabase database;
    private final Scanner scanner;

    private final cli.agenda.tasks.repository.TaskRepository taskRepository;

    private final CreateTaskService createTaskService;
    private final ListPendingTasksService listPendingTasksService;
    private final ListCompletedTasksService listCompletedTasksService;
    private final ListAllTasksService listAllTasksService;
    private final UpdateTaskService updateTaskService;
    private final DeleteTaskService deleteTaskService;
    private final CompleteTaskService completeTaskService;

    private final CreateTaskCli createTaskCli;
    private final ListPendingTasksCli listPendingTasksCli;
    private final ListCompletedTasksCli listCompletedTasksCli;
    private final ListAllTasksCli listAllTasksCli;
    private final UpdateTaskCli updateTaskCli;
    private final DeleteTaskCli deleteTaskCli;
    private final CompleteTaskCli completeTaskCli;

    public TaskServiceFactory(TaskDatabaseManager dbManager, Scanner scanner) {
        this.database = dbManager.getDatabase();
        this.scanner = scanner;

        var taskDAO = new MongoDBTaskDAO(database);
        this.taskRepository = new TaskRepositoryImpl(taskDAO);

        this.createTaskService = new CreateTaskService(taskRepository);
        this.listPendingTasksService = new ListPendingTasksService(taskRepository);
        this.listCompletedTasksService = new ListCompletedTasksService(taskRepository);
        this.listAllTasksService = new ListAllTasksService(taskRepository);
        this.updateTaskService = new UpdateTaskService(taskRepository);
        this.deleteTaskService = new DeleteTaskService(taskRepository);
        this.completeTaskService = new CompleteTaskService(taskRepository);

        this.createTaskCli = new CreateTaskCli(createTaskService, scanner);
        this.listPendingTasksCli = new ListPendingTasksCli(listPendingTasksService);
        this.listCompletedTasksCli = new ListCompletedTasksCli(listCompletedTasksService);
        this.listAllTasksCli = new ListAllTasksCli(listAllTasksService);
        this.updateTaskCli = new UpdateTaskCli(updateTaskService, scanner);
        this.deleteTaskCli = new DeleteTaskCli(deleteTaskService, scanner);
        this.completeTaskCli = new CompleteTaskCli(completeTaskService, scanner);
    }

    public CreateTaskCli getCreateTaskCli() { return createTaskCli; }
    public ListPendingTasksCli getListPendingTasksCli() { return listPendingTasksCli; }
    public ListCompletedTasksCli getListCompletedTasksCli() { return listCompletedTasksCli; }
    public ListAllTasksCli getListAllTasksCli() { return listAllTasksCli; }
    public UpdateTaskCli getUpdateTaskCli() { return updateTaskCli; }
    public DeleteTaskCli getDeleteTaskCli() { return deleteTaskCli; }
    public CompleteTaskCli getCompleteTaskCli() { return completeTaskCli; }
}