package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.mapper.TaskMapper;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ListCompletedTasksService {

    private final TaskRepository taskRepository;

    public ListCompletedTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> listCompletedTasks() {
        System.out.println("Getting list of completed tasks...");

        List<TaskResponse> completedTasks = taskRepository.findByStatus(Status.COMPLETED)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());

        System.out.println("Found " + completedTasks.size() + " completed tasks");
        return completedTasks;
    }
}