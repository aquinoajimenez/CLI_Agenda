package cli.agenda.tasks;


import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.ListCompletedTasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ListCompletedTasksServiceTest {

    private ListCompletedTasksService listCompletedTasksService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        listCompletedTasksService = new ListCompletedTasksService(taskRepository);
    }

    @Test
    @DisplayName("When there are completed tasks, returns all of them")
    void testListCompletedTasksWithCompletedTasks() {
        Task pending = new Task.Builder().text("Pending task").build();
        Task completed1 = new Task.Builder().text("Completed task 1").build().withCompleted();
        Task completed2 = new Task.Builder().text("Completed task 2").priority(Priority.HIGH).build().withCompleted();

        taskRepository.save(pending);
        taskRepository.save(completed1);
        taskRepository.save(completed2);

        List<TaskResponse> result = listCompletedTasksService.listCompletedTasks();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getStatus() == Status.COMPLETED));
        assertTrue(result.stream().anyMatch(t -> t.getText().equals("Completed task 1")));
        assertTrue(result.stream().anyMatch(t -> t.getText().equals("Completed task 2")));
    }

    @Test
    @DisplayName("When there are no completed tasks, returns empty list")
    void testListCompletedTasksWithNoCompletedTasks() {
        Task pending1 = new Task.Builder().text("Pending 1").build();
        Task pending2 = new Task.Builder().text("Pending 2").build();

        taskRepository.save(pending1);
        taskRepository.save(pending2);

        List<TaskResponse> result = listCompletedTasksService.listCompletedTasks();

        assertTrue(result.isEmpty());
    }

    private static class InMemoryTaskRepository implements TaskRepository {
        private final List<Task> tasks = new ArrayList<>();

        @Override
        public Task save(Task task) {
            tasks.add(task);
            return task;
        }

        @Override
        public Optional<Task> findById(String id) {
            return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks);
        }

        @Override
        public boolean deleteById(String id) {
            return tasks.removeIf(t -> t.getId().equals(id));
        }

        @Override
        public List<Task> findByStatus(Status status) {
            return tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        }
    }
}
