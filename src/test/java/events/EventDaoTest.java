package events;

<<<<<<< HEAD
import cli.agenda.events.dao.MongoEventDAO;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EventDaoTest {

    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;
    private MongoEventDAO eventDAO;

    @BeforeAll
    static void setUpAll() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("testdb");
        collection = database.getCollection("events");
    }

    @AfterAll
    static void tearDownAll() {
        if (mongoClient != null) mongoClient.close();
    }

    @BeforeEach
    void setUp() {
        collection.drop();
        eventDAO = new MongoEventDAO(collection);
    }

    private Date daysFromNow(long days) {
        return Date.from(Instant.now().plus(days, ChronoUnit.DAYS));
    }

    private Document buildEvent(String title, Date startDate, Date endDate) {
        return new Document()
                .append("title", title)
                .append("start_date", startDate)
                .append("end_date", endDate)
                .append("created_at", new Date());
    }

    private Document buildFullEvent(String title, Date startDate, Date endDate,
                                    String description, String location) {
        return buildEvent(title, startDate, endDate)
                .append("description", description)
                .append("location", location);
    }

    @Test
    @DisplayName("save() debe insertar un documento y devolver un ID válido de 24 caracteres")
    void testSave() {
        Document doc = buildEvent("Concierto de Rock", new Date(), daysFromNow(1));

        String id = eventDAO.save(doc);

        assertNotNull(id, "El ID no debería ser nulo");
        assertEquals(24, id.length(), "El ID debe tener 24 caracteres hex (formato ObjectId)");
        assertEquals(1, collection.countDocuments(), "Debe haber exactamente 1 documento en la BD");
    }

    @Test
    @DisplayName("save() debe persistir correctamente los campos opcionales")
    void testSaveWithOptionalFields() {
        Document doc = buildFullEvent(
                "Concierto", new Date(), daysFromNow(1), "Música en directo", "Auditorio"
        );

        String id = eventDAO.save(doc);
        Optional<Document> saved = eventDAO.findById(id);

        assertTrue(saved.isPresent());
        assertEquals("Música en directo", saved.get().getString("description"));
        assertEquals("Auditorio", saved.get().getString("location"));
    }

    @Test
    @DisplayName("save() múltiple debe insertar documentos independientes")
    void testSaveMultipleDocuments() {
        eventDAO.save(buildEvent("Evento 1", new Date(), daysFromNow(1)));
        eventDAO.save(buildEvent("Evento 2", new Date(), daysFromNow(2)));
        eventDAO.save(buildEvent("Evento 3", new Date(), daysFromNow(3)));

        assertEquals(3, collection.countDocuments(), "Deben existir 3 documentos en la BD");
    }

    @Test
    @DisplayName("findById() debe devolver el documento correcto cuando existe")
    void testFindById() {
        Document doc = buildEvent("Reunión de trabajo", new Date(), daysFromNow(1));
        String id = eventDAO.save(doc);

        Optional<Document> result = eventDAO.findById(id);

        assertTrue(result.isPresent(), "Debería encontrar el evento");
        assertEquals("Reunión de trabajo", result.get().getString("title"));
    }

    @Test
    @DisplayName("findById() debe devolver Optional.empty() cuando el ID es válido pero no existe")
    void testFindByIdNotFound() {
        String randomId = new ObjectId().toHexString();

        Optional<Document> result = eventDAO.findById(randomId);

        assertTrue(result.isEmpty(), "Debería devolver empty si no existe el ID");
    }

    @Test
    @DisplayName("findById() debe devolver Optional.empty() con formato de ID inválido")
    void testFindByIdInvalidFormat() {
        Optional<Document> result = eventDAO.findById("12345-id-falso");

        assertTrue(result.isEmpty(), "El try-catch debe capturar la excepción y devolver empty");
    }

    @Test
    @DisplayName("findById() debe devolver Optional.empty() con ID vacío")
    void testFindByIdEmptyId() {
        Optional<Document> result = eventDAO.findById("");

        assertTrue(result.isEmpty(), "Debe devolver empty con ID vacío");
    }

    @Test
    @DisplayName("update() debe modificar el campo indicado y devolver true")
    void testUpdateHappyPath() {
        String id = eventDAO.save(
                buildFullEvent("Título original", new Date(), daysFromNow(1), "Desc", "Madrid")
        );

        boolean result = eventDAO.update(id, new Document("title", "Título modificado"));

        assertTrue(result, "Debe devolver true confirmando la actualización");
        Document updated = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        assertEquals("Título modificado", updated.getString("title"));
    }

    @Test
    @DisplayName("update() con $set no debe borrar los campos no incluidos")
    void testUpdateShouldNotDeleteOtherFields() {
        String id = eventDAO.save(
                buildFullEvent("Título", new Date(), daysFromNow(1), "Descripción", "Madrid")
        );

        eventDAO.update(id, new Document("title", "Título nuevo"));

        Document updated = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        assertEquals("Título nuevo", updated.getString("title"));
        assertEquals("Descripción", updated.getString("description"), "La descripción no debe cambiar");
        assertEquals("Madrid", updated.getString("location"), "La ubicación no debe cambiar");
    }

    @Test
    @DisplayName("update() debe devolver false cuando el ID no existe")
    void testUpdateNotFound() {
        String randomId = new ObjectId().toHexString();

        boolean result = eventDAO.update(randomId, new Document("title", "Fantasma"));

        assertFalse(result, "Debe devolver false porque el documento no existe");
    }

    @Test
    @DisplayName("update() debe devolver false con formato de ID inválido")
    void testUpdateInvalidFormat() {
        boolean result = eventDAO.update("id-roto", new Document("title", "Nada"));

        assertFalse(result, "El try-catch debe capturar el error y devolver false");
    }

    @Test
    @DisplayName("delete() debe eliminar el documento y devolver true")
    void testDelete() {
        String id = eventDAO.save(buildEvent("Evento a borrar", new Date(), daysFromNow(1)));

        boolean result = eventDAO.delete(id);

        assertTrue(result, "Debe devolver true confirmando el borrado");
        assertEquals(0, collection.countDocuments(), "La colección debe estar vacía");
    }

    @Test
    @DisplayName("delete() debe eliminar solo el documento indicado")
    void testDeleteShouldDeleteOnlySpecifiedDocument() {
        String id1 = eventDAO.save(buildEvent("Evento 1", new Date(), daysFromNow(1)));
        eventDAO.save(buildEvent("Evento 2", new Date(), daysFromNow(2)));

        eventDAO.delete(id1);

        assertEquals(1, collection.countDocuments(), "Solo debe quedar 1 documento");
        assertTrue(eventDAO.findById(id1).isEmpty(), "El documento borrado no debe existir");
    }

    @Test
    @DisplayName("delete() debe devolver false cuando el ID no existe")
    void testDeleteNotFound() {
        String randomId = new ObjectId().toHexString();

        boolean result = eventDAO.delete(randomId);

        assertFalse(result, "Debe devolver false porque el documento no existe");
    }

    @Test
    @DisplayName("delete() debe devolver false con formato de ID inválido")
    void testDeleteInvalidFormat() {
        boolean result = eventDAO.delete("no-soy-un-object-id");

        assertFalse(result, "El try-catch debe devolver false ante un formato inválido");
    }

    @Test
    @DisplayName("findUpComing() debe devolver solo eventos con end_date futura")
    void testFindUpComingHappyPath() {
        eventDAO.save(buildEvent("Evento futuro", new Date(), daysFromNow(1)));
        eventDAO.save(buildEvent("Evento pasado", new Date(), daysFromNow(-1)));

        List<Document> result = eventDAO.findUpComing();

        assertEquals(1, result.size(), "Solo debe devolver 1 evento futuro");
        assertEquals("Evento futuro", result.get(0).getString("title"));
    }

    @Test
    @DisplayName("findUpComing() debe devolver múltiples eventos futuros")
    void testFindUpComingMultipleEvents() {
        eventDAO.save(buildEvent("Evento 1", new Date(), daysFromNow(1)));
        eventDAO.save(buildEvent("Evento 2", new Date(), daysFromNow(2)));
        eventDAO.save(buildEvent("Evento 3", new Date(), daysFromNow(3)));

        List<Document> result = eventDAO.findUpComing();

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findUpComing() debe devolver lista vacía si no hay eventos futuros")
    void testFindUpComingNoFutureEvents() {
        eventDAO.save(buildEvent("Evento pasado", new Date(), daysFromNow(-1)));

        List<Document> result = eventDAO.findUpComing();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findUpComing() debe devolver lista vacía si la colección está vacía")
    void testFindUpComingEmptyCollection() {
        assertTrue(eventDAO.findUpComing().isEmpty());
    }

    @Test
    @DisplayName("findPast() debe devolver solo eventos con end_date pasada")
    void testFindPast() {
        eventDAO.save(buildEvent("Evento pasado", new Date(), daysFromNow(-1)));
        eventDAO.save(buildEvent("Evento futuro", new Date(), daysFromNow(1)));

        List<Document> result = eventDAO.findPast();

        assertEquals(1, result.size(), "Solo debe devolver 1 evento pasado");
        assertEquals("Evento pasado", result.get(0).getString("title"));
    }

    @Test
    @DisplayName("findPast() debe devolver lista vacía si no hay eventos pasados")
    void testFindPastNoFutureEvents() {
        eventDAO.save(buildEvent("Evento futuro", new Date(), daysFromNow(1)));

        List<Document> result = eventDAO.findPast();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findPast() debe devolver lista vacía si la colección está vacía")
    void testFindPastEmptyCollection() {
        assertTrue(eventDAO.findPast().isEmpty());
    }

    @Test
    @DisplayName("findUpComing() y findPast() no deben solaparse nunca")
    void testUpcomingAndPastShouldNotOverlap() {
        eventDAO.save(buildEvent("Pasado", new Date(), daysFromNow(-1)));
        eventDAO.save(buildEvent("Futuro", new Date(), daysFromNow(1)));

        List<Document> upcoming = eventDAO.findUpComing();
        List<Document> past     = eventDAO.findPast();

        upcoming.forEach(u ->
                past.forEach(p ->
                        assertNotEquals(
                                u.getObjectId("_id"),
                                p.getObjectId("_id"),
                                "Un evento no puede estar en upcoming y past a la vez"
                        )
                )
        );
    }
=======
public class EventDaoTest {

>>>>>>> 79c21c0 (feat: dependencies added in pom, added methods in eventdao interface, added methods not finished in mongoeventdao and class eventdaotest created)
}
