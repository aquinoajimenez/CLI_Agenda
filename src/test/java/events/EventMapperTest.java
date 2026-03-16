package events;

import cli.agenda.events.mapper.EventMapper;
import cli.agenda.events.model.Event;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class EventMapperTest {

    @Test
    @DisplayName("Debería convertir un Evento a Documento y viceversa sin perder datos")
    void mapEventToDocumentAndBack() {

        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime end = start.plusHours(2).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        String mongoId = new ObjectId().toHexString();

        Event originalEvent = new Event.Builder("Concierto", start, end)
                .id(mongoId)
                .description("Música en directo")
                .location("Auditorio")
                .updatedAt(created)
                .build();

        Document doc = EventMapper.toDocument(originalEvent);
        Event restoredEvent = EventMapper.toEvent(doc);

        assertNotNull(restoredEvent);
        assertAll("Comprobando que no se ha perdido ningún dato en la traducción",
                () -> assertEquals(originalEvent.getId(), restoredEvent.getId()),
                () -> assertEquals(originalEvent.getTitle(), restoredEvent.getTitle()),
                () -> assertEquals(originalEvent.getStartDate(), restoredEvent.getStartDate()),
                () -> assertEquals(originalEvent.getEndDate(), restoredEvent.getEndDate()),
                () -> assertEquals(originalEvent.getDescription(), restoredEvent.getDescription()),
                () -> assertEquals(originalEvent.getLocation(), restoredEvent.getLocation()),
                () -> assertEquals(originalEvent.getUpdatedAt(), restoredEvent.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Debería convertir un Evento sin campos opcionales a Documento y viceversa")
    void mapEventWithOnlyRequiredFieldsToDocumentAndBack() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime end = start.plusHours(2).truncatedTo(ChronoUnit.MILLIS);

        Event originalEvent = new Event.Builder("Concierto", start, end).build();

        Document doc = EventMapper.toDocument(originalEvent);
        Event restoredEvent = EventMapper.toEvent(doc);

        assertAll("Campos opcionales deben ser null",
                () -> assertNull(restoredEvent.getId()),
                () -> assertNull(restoredEvent.getDescription()),
                () -> assertNull(restoredEvent.getLocation()),
                () -> assertNull(restoredEvent.getUpdatedAt()),
                () -> assertEquals("Concierto", restoredEvent.getTitle())
        );
    }

    @Test
    @DisplayName("toDocument() no debe incluir _id cuando el evento no tiene id")
    void toDocumentShouldNotContainIdWhenNull() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event.Builder("Concierto", start, end).build();
        Document doc = EventMapper.toDocument(event);

        assertFalse(doc.containsKey("_id"), "El documento no debe contener _id si el evento no tiene id");
    }

    @Test
    @DisplayName("toDocument() debe incluir _id cuando el evento tiene id")
    void toDocumentShouldContainIdWhenPresent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);
        String mongoId = new ObjectId().toHexString();

        Event event = new Event.Builder("Concierto", start, end).id(mongoId).build();
        Document doc = EventMapper.toDocument(event);

        assertTrue(doc.containsKey("_id"), "El documento debe contener _id si el evento tiene id");
        assertEquals(mongoId, doc.getObjectId("_id").toHexString());
    }

    @Test
    @DisplayName("toDocument() debe incluir los campos opcionales solo cuando tienen valor")
    void toDocumentShouldOnlyIncludeOptionalFieldsWhenPresent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event.Builder("Concierto", start, end)
                .description("Música en directo")
                .build();

        Document doc = EventMapper.toDocument(event);

        assertTrue(doc.containsKey("description"));
        assertFalse(doc.containsKey("location"),    "No debe incluir location si es null");
        assertFalse(doc.containsKey("updated_at"),  "No debe incluir updated_at si es null");
    }

    @Test
    @DisplayName("toEvent() debe devolver null si el documento es null")
    void toEventShouldReturnNullWhenDocumentIsNull() {
        Event result = EventMapper.toEvent(null);
        assertNull(result, "Debe devolver null si el documento es null");
    }

    @Test
    @DisplayName("toEvent() debe lanzar excepción si el título es null en el documento")
    void toEventShouldThrowExceptionWhenTitleIsNull() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        Document doc = new Document()
                .append("start_date", Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .append("end_date",   Date.from(end.atZone(ZoneId.systemDefault()).toInstant()))
                .append("created_at", new Date());

        assertThrows(IllegalArgumentException.class, () -> EventMapper.toEvent(doc),
                "Debe lanzar excepción si el título es null");
    }

    @Test
    @DisplayName("toEvent() debe lanzar excepción si start_date es null en el documento")
    void toEventShouldThrowExceptionWhenStartDateIsNull() {
        Document doc = new Document()
                .append("title",      "Concierto")
                .append("end_date",   new Date())
                .append("created_at", new Date());

        assertThrows(IllegalArgumentException.class, () -> EventMapper.toEvent(doc),
                "Debe lanzar excepción si start_date es null");
    }

    @Test
    @DisplayName("toEvent() debe lanzar excepción si end_date es null en el documento")
    void toEventShouldThrowExceptionWhenEndDateIsNull() {
        Document doc = new Document()
                .append("title",      "Concierto")
                .append("start_date", new Date())
                .append("created_at", new Date());

        assertThrows(IllegalArgumentException.class, () -> EventMapper.toEvent(doc),
                "Debe lanzar excepción si end_date es null");
    }
}
