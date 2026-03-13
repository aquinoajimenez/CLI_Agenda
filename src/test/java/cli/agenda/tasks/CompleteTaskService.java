package cli.agenda.tasks;

import cli.agenda.tasks.exception.TaskAlreadyCompletedException;
import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.CompleteTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CompleteTaskServiceTest {

    private CompleteTaskService completeTaskService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        completeTaskService = new CompleteTaskService(taskRepository);
    }

    @Test
    @DisplayName("Happy Path: Mark a pending task as completed")
    void testMarkPendingTaskAsCompleted() {
        Task task = new Task.Builder()
                .text("Task to complete")
                .priority(Priority.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();
        Task savedTask = taskRepository.save(task);
        String taskId = savedTask.getId();

        assertEquals(Status.PENDING, savedTask.getStatus());

        Task completedTask = completeTaskService.markAsCompleted(taskId);

        assertNotNull(completedTask);
        assertEquals(taskId, completedTask.getId());
        assertEquals("Task to complete", completedTask.getText());
        assertEquals(Priority.MEDIUM, completedTask.getPriority());
        assertEquals(Status.COMPLETED, completedTask.getStatus());
        assertNotNull(completedTask.getCreatedAt());

        Optional<Task> found = taskRepository.findById(taskId);
        assertTrue(found.isPresent());
        assertEquals(Status.COMPLETED, found.get().getStatus());
    }

    @Test
    @DisplayName("Happy Path: Mark a high priority task as completed")
    void testMarkHighPriorityTaskAsCompleted() {
        Task task = new Task.Builder()
                .text("High priority task")
                .priority(Priority.HIGH)
                .build();
        Task savedTask = taskRepository.save(task);

        Task completedTask = completeTaskService.markAsCompleted(savedTask.getId());

        assertEquals(Priority.HIGH, completedTask.getPriority());
        assertEquals(Status.COMPLETED, completedTask.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Mark a low priority task as completed")
    void testMarkLowPriorityTaskAsCompleted() {
        Task task = new Task.Builder()
                .text("Low priority task")
                .priority(Priority.LOW)
                .build();
        Task savedTask = taskRepository.save(task);

        Task completedTask = completeTaskService.markAsCompleted(savedTask.getId());

        assertEquals(Priority.LOW, completedTask.getPriority());
        assertEquals(Status.COMPLETED, completedTask.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Mark a task with due date as completed")
    void testMarkTaskWithDueDateAsCompleted() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(10);
        Task task = new Task.Builder()
                .text("Task with due date")
                .priority(Priority.MEDIUM)
                .dueDate(dueDate)
                .build();
        Task savedTask = taskRepository.save(task);

        Task completedTask = completeTaskService.markAsCompleted(savedTask.getId());

        assertEquals(dueDate, completedTask.getDueDate()); // Due date should remain unchanged
        assertEquals(Status.COMPLETED, completedTask.getStatus());
    }

    @Test
    @DisplayName("Happy Path: Mark a task without due date as completed")
    void testMarkTaskWithoutDueDateAsCompleted() {
        Task task = new Task.Builder()
                .text("Task without due date")
                .priority(Priority.MEDIUM)
                .dueDate(null)
                .build();
        Task savedTask = taskRepository.save(task);
        assertNull(savedTask.getDueDate());

        Task completedTask = completeTaskService.markAsCompleted(savedTask.getId());

        assertNull(completedTask.getDueDate());
        assertEquals(Status.COMPLETED, completedTask.getStatus());
    }

    @Test
    @DisplayName("Happy Path: canBeCompleted returns true for existing pending task")
    void testCanBeCompletedReturnsTrueForPendingTask() {
        Task task = new Task.Builder().text("Pending task").build();
        Task savedTask = taskRepository.save(task);

        assertTrue(completeTaskService.canBeCompleted(savedTask.getId()));
    }

    @Test
    @DisplayName("Happy Path: canBeCompleted returns false for non-existing task")
    void testCanBeCompletedReturnsFalseForNonExistingTask() {
        assertFalse(completeTaskService.canBeCompleted("non-existent-id"));
    }

    @Test
    @DisplayName("Happy Path: canBeCompleted returns false for null ID")
    void testCanBeCompletedReturnsFalseForNullId() {
        assertFalse(completeTaskService.canBeCompleted(null));
    }

    @Test
    @DisplayName("Happy Path: canBeCompleted returns false for empty ID")
    void testCanBeCompletedReturnsFalseForEmptyId() {
        assertFalse(completeTaskService.canBeCompleted(""));
        assertFalse(completeTaskService.canBeCompleted("   "));
    }

    @Test
    @DisplayName("Error Case: Mark already completed task throws TaskAlreadyCompletedException")
    void testMarkAlreadyCompletedTaskThrowsException() {
        // Arrange - Create and complete a task
        Task task = new Task.Builder().text("Already completed").build();
        Task savedTask = taskRepository.save(task);
        Task completedTask = savedTask.withCompleted();
        taskRepository.save(completedTask); // Update in repository

        TaskAlreadyCompletedException exception = assertThrows(
                TaskAlreadyCompletedException.class,
                () -> completeTaskService.markAsCompleted(savedTask.getId())
        );

        assertTrue(exception.getMessage().contains("already marked as completed"));
    }

    @Test
    @DisplayName("Error Case: Mark non-existing task throws TaskValidationException")
    void testMarkNonExistingTaskThrowsException() {
        String nonExistentId = "i-dont-exist";

        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> completeTaskService.markAsCompleted(nonExistentId)
        );

        assertEquals("No task found with ID: " + nonExistentId, exception.getMessage());
    }

    @Test
    @DisplayName("Error Case: Mark with null ID throws TaskValidationException")
    void testMarkWithNullIdThrowsException() {
        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> completeTaskService.markAsCompleted(null)
        );

        assertEquals("Task ID cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Error Case: Mark with empty ID throws TaskValidationException")
    void testMarkWithEmptyIdThrowsException() {
        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> completeTaskService.markAsCompleted("")
        );

        assertEquals("Task ID cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Error Case: Mark with whitespace ID throws TaskValidationException")
    void testMarkWithWhitespaceIdThrowsException() {
        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> completeTaskService.markAsCompleted("   ")
        );

        assertEquals("Task ID cannot be empty", exception.getMessage());
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
