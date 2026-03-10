package cli.agenda.tasks;

import cli.agenda.tasks.dao.mongodb.MongoDBTaskDAO;
import cli.agenda.tasks.model.Priority;
import cli.agenda.tasks.model.Task;
import cli.agenda.tasks.repository.impl.TaskRepositoryImpl;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceVerificationTest {

    private static final String TEST_DB = "persistence_test_db";
    private static MongoDatabase database;

    @BeforeAll
    static void initDatabase() {
        database = MongoClients.create("mongodb://localhost:27017")
                .getDatabase(TEST_DB);
    }

    @AfterAll
    static void cleanup() {
        database.drop();
    }

    @Test
    @DisplayName("Les dades persisteixen després de tancar connexió")
    void testDataSurvivesConnectionClose() {
        String taskId;

        // FASE 1: Inserir una tasca
        {
            var dao = new MongoDBTaskDAO(database);
            var repository = new TaskRepositoryImpl(dao);

            Task task = new Task.Builder()
                    .text("Tasca que ha de sobreviure")
                    .priority(Priority.HIGH)
                    .dueDate(LocalDateTime.now().plusDays(30))
                    .build();

            Task saved = repository.save(task);
            taskId = saved.getId();
            assertNotNull(taskId);

            System.out.println("✅ Tasca insertada amb ID: " + taskId);
        } // dao i repository es tanquen aquí (surten d'àmbit)

        // FASE 2: Crear NOVA connexió i recuperar la tasca
        {
            var newDao = new MongoDBTaskDAO(database);
            var newRepository = new TaskRepositoryImpl(newDao);

            var found = newRepository.findById(taskId);

            assertTrue(found.isPresent(), "❌ La tasca hauria de persistir!");
            assertEquals("Tasca que ha de sobreviure", found.get().getText());
            assertEquals(Priority.HIGH, found.get().getPriority());

            System.out.println("✅ Tasca recuperada amb èxit després de reconnectar!");
        }
    }
}
