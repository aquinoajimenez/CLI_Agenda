package cli.agenda.tasks;

import cli.agenda.tasks.exception.TaskValidationException;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.DeleteTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DeleteTaskServiceTest {

    private DeleteTaskService deleteTaskService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        deleteTaskService = new DeleteTaskService(taskRepository);
    }

    @Test
    @DisplayName("Happy Path: Delete an existing task by ID")
    void testDeleteExistingTask() {
        Task task = new Task.Builder()
                .text("Task to delete")
                .priority(Priority.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();
        Task savedTask = taskRepository.save(task);
        String taskId = savedTask.getId();

        assertTrue(taskRepository.findById(taskId).isPresent());
        assertEquals(1, taskRepository.findAll().size());

        Task deletedTask = deleteTaskService.deleteTask(taskId);

        assertNotNull(deletedTask);
        assertEquals(taskId, deletedTask.getId());
        assertEquals("Task to delete", deletedTask.getText());

        assertFalse(taskRepository.findById(taskId).isPresent());
        assertEquals(0, taskRepository.findAll().size());
    }

    @Test
    @DisplayName("Happy Path: Delete a task with all fields populated")
    void testDeleteTaskWithAllFields() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(10);
        LocalDateTime creationDate = LocalDateTime.now().minusDays(1);

        Task task = new Task.Builder()
                .id("test-complete-id")
                .text("Complete task with all fields")
                .priority(Priority.HIGH)
                .dueDate(dueDate)
                .status(Status.PENDING)
                .createdAt(creationDate)
                .build();
        Task savedTask = taskRepository.save(task);
        String taskId = savedTask.getId();

        Task deletedTask = deleteTaskService.deleteTask(taskId);

        assertNotNull(deletedTask);
        assertEquals(taskId, deletedTask.getId());
        assertEquals("Complete task with all fields", deletedTask.getText());
        assertEquals(Priority.HIGH, deletedTask.getPriority());
        assertEquals(dueDate, deletedTask.getDueDate());
        assertEquals(Status.PENDING, deletedTask.getStatus());
        assertEquals(creationDate, deletedTask.getCreatedAt());

        assertFalse(taskRepository.findById(taskId).isPresent());
    }

    @Test
    @DisplayName("Happy Path: Delete a completed task")
    void testDeleteCompletedTask() {
        Task task = new Task.Builder()
                .text("Completed task to delete")
                .priority(Priority.LOW)
                .build();
        Task savedTask = taskRepository.save(task);
        Task completedTask = savedTask.withCompleted();
        taskRepository.save(completedTask);
        String taskId = completedTask.getId();

        Task deletedTask = deleteTaskService.deleteTask(taskId);

        assertNotNull(deletedTask);
        assertEquals(taskId, deletedTask.getId());
        assertEquals(Status.COMPLETED, deletedTask.getStatus());

        assertFalse(taskRepository.findById(taskId).isPresent());
    }

    @Test
    @DisplayName("Happy Path: Delete a task with no due date")
    void testDeleteTaskWithNoDueDate() {
        Task task = new Task.Builder()
                .text("Task without due date")
                .priority(Priority.MEDIUM)
                .dueDate(null)
                .build();
        Task savedTask = taskRepository.save(task);
        String taskId = savedTask.getId();

        assertNull(savedTask.getDueDate());

        Task deletedTask = deleteTaskService.deleteTask(taskId);

        assertNotNull(deletedTask);
        assertNull(deletedTask.getDueDate());
        assertFalse(taskRepository.findById(taskId).isPresent());
    }

    @Test
    @DisplayName("Happy Path: Delete multiple tasks sequentially")
    void testDeleteMultipleTasks() {
        Task task1 = new Task.Builder().text("Task 1").priority(Priority.LOW).build();
        Task task2 = new Task.Builder().text("Task 2").priority(Priority.MEDIUM).build();
        Task task3 = new Task.Builder().text("Task 3").priority(Priority.HIGH).build();

        Task saved1 = taskRepository.save(task1);
        Task saved2 = taskRepository.save(task2);
        Task saved3 = taskRepository.save(task3);

        assertEquals(3, taskRepository.findAll().size());

        Task deleted2 = deleteTaskService.deleteTask(saved2.getId());
        assertNotNull(deleted2);
        assertEquals("Task 2", deleted2.getText());
        assertEquals(2, taskRepository.findAll().size());
        assertFalse(taskRepository.findById(saved2.getId()).isPresent());
        assertTrue(taskRepository.findById(saved1.getId()).isPresent());
        assertTrue(taskRepository.findById(saved3.getId()).isPresent());

        Task deleted1 = deleteTaskService.deleteTask(saved1.getId());
        assertNotNull(deleted1);
        assertEquals("Task 1", deleted1.getText());
        assertEquals(1, taskRepository.findAll().size());
        assertFalse(taskRepository.findById(saved1.getId()).isPresent());
        assertTrue(taskRepository.findById(saved3.getId()).isPresent());

        Task deleted3 = deleteTaskService.deleteTask(saved3.getId());
        assertNotNull(deleted3);
        assertEquals("Task 3", deleted3.getText());
        assertEquals(0, taskRepository.findAll().size());
        assertFalse(taskRepository.findById(saved3.getId()).isPresent());
    }

    @Test
    @DisplayName("Happy Path: taskExists returns true for existing task")
    void testTaskExistsReturnsTrueForExistingTask() {
        Task task = new Task.Builder().text("Existing task").build();
        Task savedTask = taskRepository.save(task);

        assertTrue(deleteTaskService.taskExists(savedTask.getId()));
    }

    @Test
    @DisplayName("Happy Path: taskExists returns false for non-existing task")
    void testTaskExistsReturnsFalseForNonExistingTask() {
        assertFalse(deleteTaskService.taskExists("non-existent-id"));
    }

    @Test
    @DisplayName("Error Case: Delete non-existing task throws TaskValidationException")
    void testDeleteNonExistingTaskThrowsException() {
        String nonExistentId = "i-dont-exist";

        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> deleteTaskService.deleteTask(nonExistentId)
        );

        assertEquals("No task found with ID: " + nonExistentId, exception.getMessage());
    }

    @Test
    @DisplayName("Error Case: Delete with null ID throws exception")
    void testDeleteWithNullIdThrowsException() {
        assertThrows(Exception.class, () -> deleteTaskService.deleteTask(null));
    }

    @Test
    @DisplayName("Error Case: Delete with empty ID throws exception")
    void testDeleteWithEmptyIdThrowsException() {
        assertThrows(TaskValidationException.class, () -> deleteTaskService.deleteTask(""));
    }

    private static class InMemoryTaskRepository implements TaskRepository {
        private final List<Task> tasks = new ArrayList<>();

        @Override
        public Task save(Task task) {
            // Remove existing if present (for update)
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
