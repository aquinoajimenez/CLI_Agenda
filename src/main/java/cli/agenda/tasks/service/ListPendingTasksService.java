package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.mapper.TaskMapper;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ListPendingTasksService {

    private final TaskRepository taskRepository;

    public ListPendingTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> listPendingTasks() {
        System.out.println("Getting list of pending tasks...");

        List<TaskResponse> pendingTasks = taskRepository.findByStatus(Status.PENDING)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());

        System.out.println("Found " + pendingTasks.size() + " pending tasks");
        return pendingTasks;
    }
}
