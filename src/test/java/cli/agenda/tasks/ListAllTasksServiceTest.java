package cli.agenda.tasks;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.ListAllTasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ListAllTasksServiceTest {

    private ListAllTasksService listAllTasksService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        listAllTasksService = new ListAllTasksService(taskRepository);
    }

    @Test
    @DisplayName("When there are tasks, returns all of them")
    void testListAllTasksWithTasks() {
        Task task1 = new Task.Builder().text("Task 1").priority(Priority.HIGH).build();
        Task task2 = new Task.Builder().text("Task 2").priority(Priority.MEDIUM).build();
        Task task3 = new Task.Builder().text("Task 3").priority(Priority.LOW).build().withCompleted();

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        List<TaskResponse> result = listAllTasksService.listAllTasks();

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getText().equals("Task 1")));
        assertTrue(result.stream().anyMatch(t -> t.getText().equals("Task 2")));
        assertTrue(result.stream().anyMatch(t -> t.getText().equals("Task 3")));

        assertTrue(result.stream().anyMatch(t -> t.getStatus() == Status.PENDING));
        assertTrue(result.stream().anyMatch(t -> t.getStatus() == Status.COMPLETED));
    }

    @Test
    @DisplayName("When there are no tasks, returns empty list")
    void testListAllTasksWithNoTasks() {
        List<TaskResponse> result = listAllTasksService.listAllTasks();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Returns correct count of pending and completed tasks")
    void testListAllTasksCounts() {
        Task pending1 = new Task.Builder().text("Pending 1").build();
        Task pending2 = new Task.Builder().text("Pending 2").build();
        Task completed1 = new Task.Builder().text("Completed 1").build().withCompleted();
        Task completed2 = new Task.Builder().text("Completed 2").build().withCompleted();

        taskRepository.save(pending1);
        taskRepository.save(pending2);
        taskRepository.save(completed1);
        taskRepository.save(completed2);

        List<TaskResponse> result = listAllTasksService.listAllTasks();

        assertEquals(4, result.size());

        long pendingCount = result.stream().filter(t -> t.getStatus() == Status.PENDING).count();
        long completedCount = result.stream().filter(t -> t.getStatus() == Status.COMPLETED).count();

        assertEquals(2, pendingCount);
        assertEquals(2, completedCount);
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
