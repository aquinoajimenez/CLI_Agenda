package cli.agenda.tasks.dao.mongodb;

import cli.agenda.tasks.dao.TaskDAO;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Status;
import cli.agenda.tasks.model.Task;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per verificar que MongoDBTaskDAO realment persisteix les dades.
 * Aquest test connecta amb una base de dades REAL (no en memòria).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDBTaskDAOTest {

    private TaskDAO taskDAO;
    private MongoDatabase database;
    private final String TEST_DB_NAME = "test_db_" + UUID.randomUUID().toString().substring(0, 8);

    @BeforeAll
    void setUp() {
        // Connectar a MongoDB real (sense autenticació)
        var mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase(TEST_DB_NAME);
        taskDAO = new MongoDBTaskDAO(database);
        System.out.println("✅ Connexió a base de dades de test: " + TEST_DB_NAME);
    }

    @AfterEach
    void cleanUp() {
        // Netejar després de cada test
        database.getCollection("tasks").deleteMany(new org.bson.Document());
    }

    @AfterAll
    void tearDown() {
        // Eliminar base de dades de test
        database.drop();
        System.out.println("✅ Base de dades de test eliminada: " + TEST_DB_NAME);
    }

    @Test
    @DisplayName("Insert ha de persistir la tasca i retornar-la amb ID")
    void testInsertPersistsTask() {
        // Arrange
        Task task = new Task.Builder()
                .text("Test persistència")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(7))
                .build();

        // Act
        Task savedTask = taskDAO.insert(task);

        // Assert
        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());

        // Verificar que realment està a la BD
        Optional<Task> found = taskDAO.findById(savedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Test persistència", found.get().getText());
        assertEquals(Priority.HIGH, found.get().getPriority());
    }

    @Test
    @DisplayName("Insert ha de llençar excepció si MongoDB falla")
    void testInsertThrowsExceptionOnFailure() {
        // TODO: Provar cas d'error (tancant connexió, etc.)
    }

    @Test
    @DisplayName("findById retorna la tasca si existeix")
    void testFindByIdReturnsTaskWhenExists() {
        // Arrange - Insertar una tasca primer
        Task task = new Task.Builder()
                .text("Tasca per cercar")
                .build();
        Task savedTask = taskDAO.insert(task);

        // Act
        Optional<Task> found = taskDAO.findById(savedTask.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Tasca per cercar", found.get().getText());
    }

    @Test
    @DisplayName("findById retorna buit si la tasca no existeix")
    void testFindByIdReturnsEmptyWhenNotExists() {
        Optional<Task> found = taskDAO.findById("id_que_no_existeix");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findAll retorna totes les tasques")
    void testFindAllReturnsAllTasks() {
        // Arrange
        taskDAO.insert(new Task.Builder().text("Tasca 1").build());
        taskDAO.insert(new Task.Builder().text("Tasca 2").priority(Priority.HIGH).build());
        taskDAO.insert(new Task.Builder().text("Tasca 3").build());

        // Act
        List<Task> tasks = taskDAO.findAll();

        // Assert
        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("findByStatus retorna només tasques amb l'estat indicat")
    void testFindByStatusFiltersCorrectly() {
        // Arrange
        taskDAO.insert(new Task.Builder().text("Pending 1").build());
        taskDAO.insert(new Task.Builder().text("Pending 2").build());
        taskDAO.insert(new Task.Builder().text("Completed 1").build().withCompleted());

        // Act
        List<Task> pendingTasks = taskDAO.findByStatus(Status.PENDING);
        List<Task> completedTasks = taskDAO.findByStatus(Status.COMPLETED);

        // Assert
        assertEquals(2, pendingTasks.size());
        assertEquals(1, completedTasks.size());
    }

    @Test
    @DisplayName("update modifica correctament una tasca existent")
    void testUpdateExistingTask() {
        // Arrange
        Task task = new Task.Builder()
                .text("Original")
                .priority(Priority.LOW)
                .build();
        Task savedTask = taskDAO.insert(task);

        // Modificar la tasca
        Task updatedTask = new Task.Builder(savedTask)
                .text("Modificada")
                .priority(Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(10))
                .build();

        // Act
        boolean result = taskDAO.update(updatedTask);

        // Assert
        assertTrue(result);

        Optional<Task> found = taskDAO.findById(savedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Modificada", found.get().getText());
        assertEquals(Priority.HIGH, found.get().getPriority());
        assertNotNull(found.get().getDueDate());
    }

    @Test
    @DisplayName("update retorna false si la tasca no existeix")
    void testUpdateNonExistingTask() {
        Task nonExistingTask = new Task.Builder()
                .id("id_inexistent")
                .text("No existeix")
                .build();

        boolean result = taskDAO.update(nonExistingTask);
        assertFalse(result);
    }

    @Test
    @DisplayName("deleteById elimina la tasca si existeix")
    void testDeleteExistingTask() {
        // Arrange
        Task task = taskDAO.insert(new Task.Builder().text("A eliminar").build());

        // Act
        boolean deleted = taskDAO.deleteById(task.getId());

        // Assert
        assertTrue(deleted);
        assertFalse(taskDAO.findById(task.getId()).isPresent());
    }

    @Test
    @DisplayName("deleteById retorna false si la tasca no existeix")
    void testDeleteNonExistingTask() {
        boolean deleted = taskDAO.deleteById("id_inexistent");
        assertFalse(deleted);
    }
}
