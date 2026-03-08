package cli.agenda.tasks;

import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.ListPendingTasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListPendingTasksServiceTest{

    private ListPendingTasksService listPendingTasksService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        listPendingTasksService = new ListPendingTasksService(taskRepository);
    }

    @Test
    @DisplayName("Quan hi ha tasques pendents, les retorna totes")
    void testListPendingTasksWithPendingTasks() {
        Task pending1 = new Task.Builder().text("Tasca pendent 1").build();
        Task pending2 = new Task.Builder().text("Tasca pendent 2").priority(Priority.HIGH).build();
        Task completed = new Task.Builder().text("Tasca completada").build().withCompleted();

        taskRepository.save(pending1);
        taskRepository.save(pending2);
        taskRepository.save(completed);

        List<TaskResponse> result = listPendingTasksService.listPendingTasks();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getStatus() == Status.PENDING));
    }

    @Test
    @DisplayName("Quan no hi ha tasques pendents, retorna llista buida")
    void testListPendingTasksWithNoPendingTasks() {
        Task completed1 = new Task.Builder().text("Completada 1").build().withCompleted();
        Task completed2 = new Task.Builder().text("Completada 2").build().withCompleted();

        taskRepository.save(completed1);
        taskRepository.save(completed2);

        List<TaskResponse> result = listPendingTasksService.listPendingTasks();

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
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }
}
