package cli.agenda.tasks.service;

import cli.agenda.tasks.dto.CreateTaskRequest;
import cli.agenda.tasks.dto.TaskResponse;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @DisplayName("Happy Path: Crear una tasca amb tots els camps omplerts")
    void testCreateTaskWithAllFields() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateTaskRequest request = new CreateTaskRequest(
                "Comprar llet",
                dueDate,
                Priority.HIGH
        );

        TaskResponse response = createTaskService.createTask(request);

        assertNotNull(response, "La resposta no hauria de ser null");
        assertNotNull(response.getId(), "L'ID no hauria de ser null");
        assertEquals("Comprar llet", response.getText(), "El text hauria de coincidir");
        assertEquals(dueDate, response.getDueDate(), "La data hauria de coincidir");
        assertEquals(Priority.HIGH, response.getPriority(), "La prioritat hauria de ser HIGH");
        assertNotNull(response.getCreatedAt(), "La data de creació no hauria de ser null");
    }

    @Test
    @DisplayName("Happy Path: Crear una tasca només amb el text obligatori")
    void testCreateTaskWithOnlyRequiredFields() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Reunió d'equip",
                null,  // dueDate opcional
                null   // priority opcional (hauria d'agafar MEDIUM per defecte)
        );

        TaskResponse response = createTaskService.createTask(request);

        assertNotNull(response, "La resposta no hauria de ser null");
        assertEquals("Reunió d'equip", response.getText(), "El text hauria de coincidir");
        assertNull(response.getDueDate(), "La data hauria de ser null");
        assertEquals(Priority.MEDIUM, response.getPriority(),
                "La prioritat hauria de ser MEDIUM per defecte");
        assertNotNull(response.getCreatedAt(), "La data de creació no hauria de ser null");
    }

    @Test
    @DisplayName("Happy Path: Crear múltiples tasques i verificar IDs únics")
    void testCreateMultipleTasks() {
        CreateTaskRequest request1 = new CreateTaskRequest("Tasca 1", null, Priority.LOW);
        CreateTaskRequest request2 = new CreateTaskRequest("Tasca 2", null, Priority.MEDIUM);
        CreateTaskRequest request3 = new CreateTaskRequest("Tasca 3", null, Priority.HIGH);

        TaskResponse response1 = createTaskService.createTask(request1);
        TaskResponse response2 = createTaskService.createTask(request2);
        TaskResponse response3 = createTaskService.createTask(request3);

        assertNotNull(response1.getId());
        assertNotNull(response2.getId());
        assertNotNull(response3.getId());

        assertNotEquals(response1.getId(), response2.getId(),
                "Les tasques haurien de tenir IDs diferents");
        assertNotEquals(response1.getId(), response3.getId(),
                "Les tasques haurien de tenir IDs diferents");
        assertNotEquals(response2.getId(), response3.getId(),
                "Les tasques haurien de tenir IDs diferents");
    }

    @Test
    @DisplayName("Happy Path: Verificar que la tasca es guarda correctament al repositori")
    void testTaskIsSavedInRepository() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Tasca per verificar",
                LocalDateTime.now(),
                Priority.MEDIUM
        );

        TaskResponse response = createTaskService.createTask(request);

        Optional<Task> foundTask = taskRepository.findById(response.getId());

        assertTrue(foundTask.isPresent(), "La tasca hauria d'existir al repositori");
        assertEquals(response.getText(), foundTask.get().getText(),
                "El text hauria de coincidir");
        assertEquals(response.getPriority(), foundTask.get().getPriority(),
                "La prioritat hauria de coincidir");
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
    }
}