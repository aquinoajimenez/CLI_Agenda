package cli.agenda.tasks;

import cli.agenda.tasks.dto.UpdateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.UpdateTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UpdateTaskServiceTest {

    private UpdateTaskService updateTaskService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        updateTaskService = new UpdateTaskService(taskRepository);
    }

    @Test
    @DisplayName("Happy Path: Update ALL fields of an existing task")
    void testUpdateAllFields() {
        Task originalTask = new Task.Builder()
                .text("Original task")
                .priority(Priority.LOW)
                .dueDate(LocalDateTime.now().plusDays(10))
                .build();
        Task savedTask = taskRepository.save(originalTask);

        LocalDateTime newDueDate = LocalDateTime.now().plusDays(20);
        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                "Updated task text",
                newDueDate,
                Priority.HIGH,
                true
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertNotNull(response);
        assertEquals(savedTask.getId(), response.getId());
        assertEquals("Updated task text", response.getText());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(newDueDate, response.getDueDate());
        assertEquals(Status.COMPLETED, response.getStatus());

        Optional<Task> found = taskRepository.findById(savedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated task text", found.get().getText());
        assertEquals(Priority.HIGH, found.get().getPriority());
        assertEquals(Status.COMPLETED, found.get().getStatus());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY text field (others unchanged)")
    void testUpdateOnlyText() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(15);
        Task originalTask = new Task.Builder()
                .text("Original text")
                .priority(Priority.HIGH)
                .dueDate(dueDate)
                .build();
        Task savedTask = taskRepository.save(originalTask);

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                "Updated text only",
                null,
                null,
                null
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals("Updated text only", response.getText());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(Status.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY priority field")
    void testUpdateOnlyPriority() {
        // Arrange
        Task originalTask = new Task.Builder()
                .text("Task with LOW priority")
                .priority(Priority.LOW)
                .build();
        Task savedTask = taskRepository.save(originalTask);

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                null,
                null,
                Priority.HIGH,
                null
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals("Task with LOW priority", response.getText());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(Status.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY due date (add due date to task with none)")
    void testUpdateAddDueDate() {
        Task originalTask = new Task.Builder()
                .text("Task without due date")
                .priority(Priority.MEDIUM)
                .build();
        Task savedTask = taskRepository.save(originalTask);
        assertNull(savedTask.getDueDate());

        LocalDateTime newDueDate = LocalDateTime.now().plusDays(5);
        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                null,
                newDueDate,
                null,
                null
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals(newDueDate, response.getDueDate());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY due date (remove due date)")
    void testUpdateRemoveDueDate() {
        LocalDateTime originalDueDate = LocalDateTime.now().plusDays(10);
        Task originalTask = new Task.Builder()
                .text("Task with due date")
                .priority(Priority.HIGH)
                .dueDate(originalDueDate)
                .build();
        Task savedTask = taskRepository.save(originalTask);
        assertNotNull(savedTask.getDueDate());

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                null,
                null,
                null,
                null
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals(originalDueDate, response.getDueDate());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY status (mark as completed)")
    void testUpdateMarkAsCompleted() {
        Task originalTask = new Task.Builder()
                .text("Task to complete")
                .priority(Priority.LOW)
                .build();
        Task savedTask = taskRepository.save(originalTask);
        assertEquals(Status.PENDING, savedTask.getStatus());

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                null,
                null,
                null,
                true
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals(Status.COMPLETED, response.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Update ONLY status (mark as pending from completed)")
    void testUpdateMarkAsPending() {
        // Arrange - Create completed task
        Task originalTask = new Task.Builder()
                .text("Completed task")
                .priority(Priority.HIGH)
                .build()
                .withCompleted();  // Mark as completed
        Task savedTask = taskRepository.save(originalTask);
        assertEquals(Status.COMPLETED, savedTask.getStatus());

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                null,
                null,
                null,
                false
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals(Status.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Update multiple fields (text + priority)")
    void testUpdateMultipleFields() {
        LocalDateTime originalDueDate = LocalDateTime.now().plusDays(7);
        Task originalTask = new Task.Builder()
                .text("Original")
                .priority(Priority.LOW)
                .dueDate(originalDueDate)
                .build();
        Task savedTask = taskRepository.save(originalTask);

        UpdateTaskRequest request = new UpdateTaskRequest(
                savedTask.getId(),
                "New text",
                null,
                Priority.MEDIUM,
                null
        );

        TaskResponse response = updateTaskService.updateTask(request);

        assertEquals("New text", response.getText());
        assertEquals(Priority.MEDIUM, response.getPriority());
        assertEquals(originalDueDate, response.getDueDate());
        assertEquals(Status.PENDING, response.getStatus());
    }

    private static class InMemoryTaskRepository implements TaskRepository {
        private final List<Task> tasks = new ArrayList<>();

        @Override
        public Task save(Task task) {
            tasks.removeIf(t -> t.getId().equals(task.getId()));
            tasks.add(task);
            return task;
        }

        @Override
        public Optional<Task> findById(String id) {
            return tasks.stream()
                    .filter(task -> task.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks);
        }

        @Override
        public boolean deleteById(String id) {
            return tasks.removeIf(task -> task.getId().equals(id));
        }

        @Override
        public List<Task> findByStatus(Status status) {
            return tasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(Collectors.toList());
        }
    }
}
