package cli.agenda.tasks;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import cli.agenda.tasks.service.CreateTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CreateTaskServiceTest {

    private CreateTaskService createTaskService;
    private InMemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        createTaskService = new CreateTaskService(taskRepository);
    }

    @Test
    @DisplayName("Happy Path: Create a task with all fields filled")
    void testCreateTaskWithAllFields() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateTaskRequest request = new CreateTaskRequest(
                "Buy milk",
                dueDate,
                Priority.HIGH
        );

        TaskResponse response = createTaskService.createTask(request);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getId(), "ID should not be null");
        assertEquals("Buy milk", response.getText(), "Text should match");
        assertEquals(dueDate, response.getDueDate(), "Due date should match");
        assertEquals(Priority.HIGH, response.getPriority(), "Priority should be HIGH");
        assertNotNull(response.getCreatedAt(), "Creation date should not be null");
    }

    @Test
    @DisplayName("Happy Path: Create a task with only required fields")
    void testCreateTaskWithOnlyRequiredFields() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Team meeting",
                null,
                null
        );

        TaskResponse response = createTaskService.createTask(request);

        assertNotNull(response, "Response should not be null");
        assertEquals("Team meeting", response.getText(), "Text should match");
        assertNull(response.getDueDate(), "Due date should be null");
        assertEquals(Priority.MEDIUM, response.getPriority(),
                "Priority should default to MEDIUM");
        assertNotNull(response.getCreatedAt(), "Creation date should not be null");
    }

    @Test
    @DisplayName("Happy Path: Create multiple tasks and verify unique IDs")
    void testCreateMultipleTasks() {
        CreateTaskRequest request1 = new CreateTaskRequest("Task 1", null, Priority.LOW);
        CreateTaskRequest request2 = new CreateTaskRequest("Task 2", null, Priority.MEDIUM);
        CreateTaskRequest request3 = new CreateTaskRequest("Task 3", null, Priority.HIGH);

        TaskResponse response1 = createTaskService.createTask(request1);
        TaskResponse response2 = createTaskService.createTask(request2);
        TaskResponse response3 = createTaskService.createTask(request3);

        assertNotNull(response1.getId());
        assertNotNull(response2.getId());
        assertNotNull(response3.getId());

        assertNotEquals(response1.getId(), response2.getId(),
                "Tasks should have different IDs");
        assertNotEquals(response1.getId(), response3.getId(),
                "Tasks should have different IDs");
        assertNotEquals(response2.getId(), response3.getId(),
                "Tasks should have different IDs");
    }

    @Test
    @DisplayName("Happy Path: Verify task is properly saved in repository")
    void testTaskIsSavedInRepository() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Task to verify",
                LocalDateTime.now(),
                Priority.MEDIUM
        );

        TaskResponse response = createTaskService.createTask(request);

        Optional<Task> foundTask = taskRepository.findById(response.getId());

        assertTrue(foundTask.isPresent(), "Task should exist in repository");
        assertEquals(response.getText(), foundTask.get().getText(),
                "Text should match");
        assertEquals(response.getPriority(), foundTask.get().getPriority(),
                "Priority should match");
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