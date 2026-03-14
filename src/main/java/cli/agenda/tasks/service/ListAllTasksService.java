package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.mapper.TaskMapper;
import cli.agenda.tasks.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ListAllTasksService {

    private final TaskRepository taskRepository;

    public ListAllTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> listAllTasks() {
        System.out.println("Retrieving all tasks...");

        List<TaskResponse> allTasks = taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());

        System.out.println("Found " + allTasks.size() + " total tasks");
        return allTasks;
    }
}