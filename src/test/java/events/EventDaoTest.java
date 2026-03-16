package events;

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

    // ... (tots els altres tests es queden igual) ...

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
}