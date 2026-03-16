package cli.agenda.tasks.repository.impl;

import cli.agenda.tasks.dao.TaskDAO;
import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.TaskRepository;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskRepositoryImplTest {

    private TaskRepository taskRepository;
    private MongoDatabase database;
    private final String TEST_DB_NAME = "test_repo_" + UUID.randomUUID().toString().substring(0, 8);

    @BeforeAll
    void setUp() {
        var mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase(TEST_DB_NAME);

        TaskDAO taskDAO = new MongoDBTaskDAO(database);
        taskRepository = new TaskRepositoryImpl(taskDAO);

        System.out.println("✅ Test repository inicialitzat amb BD: " + TEST_DB_NAME);
    }

    @AfterEach
    void cleanUp() {
        database.getCollection("tasks").deleteMany(new org.bson.Document());
    }

    @AfterAll
    void tearDown() {
        database.drop();
        System.out.println("✅ Base de dades de test eliminada");
    }

    @Test
    @DisplayName("save ha d'inserir una tasca nova si no té ID")
    void testSaveInsertsNewTask() {
        // Arrange
        Task newTask = new Task.Builder()
                .text("Tasca nova")
                .priority(Priority.MEDIUM)
                .build();

        Task savedTask = taskRepository.save(newTask);

        assertNotNull(savedTask.getId());

        Optional<Task> found = taskRepository.findById(savedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Tasca nova", found.get().getText());
    }

    @Test
    @DisplayName("save ha d'actualitzar una tasca existent si té ID")
    void testSaveUpdatesExistingTask() {
        Task original = new Task.Builder()
                .text("Original")
                .priority(Priority.LOW)
                .build();
        Task saved = taskRepository.save(original);

        Task toUpdate = new Task.Builder(saved)
                .text("Actualitzada")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();

        Task updated = taskRepository.save(toUpdate);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Actualitzada", updated.getText());
        assertEquals(Priority.HIGH, updated.getPriority());

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Actualitzada", found.get().getText());
    }

    @Test
    @DisplayName("findAll retorna totes les tasques")
    void testFindAll() {
        taskRepository.save(new Task.Builder().text("Tasca 1").build());
        taskRepository.save(new Task.Builder().text("Tasca 2").build());
        taskRepository.save(new Task.Builder().text("Tasca 3").build());

        List<Task> tasks = taskRepository.findAll();

        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("findByStatus retorna tasques filtrades correctament")
    void testFindByStatus() {
        // Arrange
        taskRepository.save(new Task.Builder().text("Pendent 1").build());
        taskRepository.save(new Task.Builder().text("Pendent 2").build());
        Task completed = taskRepository.save(new Task.Builder().text("Completada").build());
        taskRepository.save(completed.withCompleted());

        List<Task> pending = taskRepository.findByStatus(Status.PENDING);
        List<Task> completedTasks = taskRepository.findByStatus(Status.COMPLETED);

        assertEquals(2, pending.size());
        assertEquals(1, completedTasks.size());
        assertEquals("Completada", completedTasks.get(0).getText());
    }

    @Test
    @DisplayName("deleteById elimina la tasca")
    void testDeleteById() {
        Task task = taskRepository.save(new Task.Builder().text("A eliminar").build());

        boolean deleted = taskRepository.deleteById(task.getId());

        assertTrue(deleted);
        assertFalse(taskRepository.findById(task.getId()).isPresent());
    }
}