package events;

import cli.agenda.events.dao.EventDao;
import cli.agenda.events.model.Event;
import cli.agenda.events.repository.EventRepositoryImpl;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryImplTest {

    @Mock
    private EventDao eventDao;

    private EventRepositoryImpl eventRepository;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END   = LocalDateTime.now().plusDays(2);
    private static final String FAKE_ID      = new ObjectId().toHexString();

    @BeforeEach
    void setUp() {
        eventRepository = new EventRepositoryImpl(eventDao);
    }

    private Event buildEvent(String id) {
        return new Event.Builder("Concierto", START, END)
                .id(id)
                .description("Descripción")
                .location("Madrid")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Event buildEventWithoutId() {
        return new Event.Builder("Concierto", START, END)
                .description("Descripción")
                .location("Madrid")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Document buildDocument(String id) {
        return new Document()
                .append("_id", new ObjectId(id))
                .append("title", "Concierto")
                .append("start_date", toDate(START))
                .append("end_date", toDate(END))
                .append("description", "Descripción")
                .append("location", "Madrid")
                .append("created_at", new Date());
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    @DisplayName("save() debe persistir el evento y devolver el Event con ID generado")
    void testSaveReturnsEventWithGeneratedId() {
        Event event = buildEventWithoutId();
        when(eventDao.save(any(Document.class))).thenReturn(FAKE_ID);

        Event result = eventRepository.save(event);

        assertNotNull(result);
        assertEquals(FAKE_ID, result.getId());
        assertEquals("Concierto", result.getTitle());
        verify(eventDao, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("save() debe preservar todos los campos del evento original")
    void testSavePreservesAllFields() {
        Event event = buildEventWithoutId();
        when(eventDao.save(any(Document.class))).thenReturn(FAKE_ID);

        Event result = eventRepository.save(event);

        assertEquals("Concierto", result.getTitle());
        assertEquals("Descripción", result.getDescription());
        assertEquals("Madrid", result.getLocation());
        assertEquals(START, result.getStartDate());
        assertEquals(END, result.getEndDate());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @DisplayName("save() debe llamar al DAO exactamente una vez")
    void testSaveCallsDaoOnce() {
        when(eventDao.save(any(Document.class))).thenReturn(FAKE_ID);

        eventRepository.save(buildEventWithoutId());

        verify(eventDao, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("save() sin campos opcionales debe devolver Event sin descripción ni ubicación")
    void testSaveWithoutOptionalFields() {
        Event event = new Event.Builder("Solo título", START, END).build();
        when(eventDao.save(any(Document.class))).thenReturn(FAKE_ID);

        Event result = eventRepository.save(event);

        assertNull(result.getDescription());
        assertNull(result.getLocation());
        assertEquals(FAKE_ID, result.getId());
    }

    @Test
    @DisplayName("findById() debe devolver Optional con Event cuando el ID existe")
    void testFindByIdReturnsEventWhenExists() {
        Document doc = buildDocument(FAKE_ID);
        when(eventDao.findById(FAKE_ID)).thenReturn(Optional.of(doc));

        Optional<Event> result = eventRepository.findById(FAKE_ID);

        assertTrue(result.isPresent());
        assertEquals(FAKE_ID, result.get().getId());
        assertEquals("Concierto", result.get().getTitle());
    }

    @Test
    @DisplayName("findById() debe devolver Optional.empty() cuando el ID no existe")
    void testFindByIdReturnsEmptyWhenNotExists() {
        when(eventDao.findById(FAKE_ID)).thenReturn(Optional.empty());

        Optional<Event> result = eventRepository.findById(FAKE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findById() debe devolver Optional.empty() con ID inválido")
    void testFindByIdReturnsEmptyWithInvalidId() {
        when(eventDao.findById("id-invalido")).thenReturn(Optional.empty());

        Optional<Event> result = eventRepository.findById("id-invalido");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findById() debe llamar al DAO con el mismo ID recibido")
    void testFindByIdCallsDaoWithCorrectId() {
        when(eventDao.findById(FAKE_ID)).thenReturn(Optional.empty());

        eventRepository.findById(FAKE_ID);

        verify(eventDao, times(1)).findById(FAKE_ID);
    }

    @Test
    @DisplayName("update() debe devolver true cuando el evento existe y se actualiza")
    void testUpdateReturnsTrueWhenSuccess() {
        Event event = buildEvent(FAKE_ID);
        when(eventDao.update(eq(FAKE_ID), any(Document.class))).thenReturn(true);

        boolean result = eventRepository.update(FAKE_ID, event);

        assertTrue(result);
        verify(eventDao, times(1)).update(eq(FAKE_ID), any(Document.class));
    }

    @Test
    @DisplayName("update() no debe incluir _id en el Document enviado al DAO")
    void testUpdateDoesNotIncludeIdInDocument() {
        Event event = buildEvent(FAKE_ID);
        when(eventDao.update(eq(FAKE_ID), any(Document.class))).thenReturn(true);

        eventRepository.update(FAKE_ID, event);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(eventDao).update(eq(FAKE_ID), captor.capture());

        assertFalse(captor.getValue().containsKey("_id"),
                "El Document de updates no debe contener _id");
    }

    @Test
    @DisplayName("update() debe devolver false cuando el ID no existe")
    void testUpdateReturnsFalseWhenNotExists() {
        Event event = buildEvent(FAKE_ID);
        when(eventDao.update(eq(FAKE_ID), any(Document.class))).thenReturn(false);

        boolean result = eventRepository.update(FAKE_ID, event);

        assertFalse(result);
    }

    @Test
    @DisplayName("update() con ID inválido debe devolver false")
    void testUpdateReturnsFalseWithInvalidId() {
        Event event = buildEvent(FAKE_ID);
        when(eventDao.update(eq("id-invalido"), any(Document.class))).thenReturn(false);

        boolean result = eventRepository.update("id-invalido", event);

        assertFalse(result);
    }

    @Test
    @DisplayName("delete() debe devolver true cuando el evento existe y se elimina")
    void testDeleteReturnsTrueWhenSuccess() {
        when(eventDao.delete(FAKE_ID)).thenReturn(true);

        boolean result = eventRepository.delete(FAKE_ID);

        assertTrue(result);
        verify(eventDao, times(1)).delete(FAKE_ID);
    }

    @Test
    @DisplayName("delete() debe devolver false cuando el ID no existe")
    void testDeleteReturnsFalseWhenNotExists() {
        when(eventDao.delete(FAKE_ID)).thenReturn(false);

        boolean result = eventRepository.delete(FAKE_ID);

        assertFalse(result);
    }

    @Test
    @DisplayName("delete() debe devolver false con ID inválido")
    void testDeleteReturnsFalseWithInvalidId() {
        when(eventDao.delete("id-invalido")).thenReturn(false);

        boolean result = eventRepository.delete("id-invalido");

        assertFalse(result);
    }

    @Test
    @DisplayName("findUpComing() debe devolver lista de Events mapeados")
    void testFindUpComingReturnsMappedEvents() {
        when(eventDao.findUpComing()).thenReturn(List.of(buildDocument(FAKE_ID)));

        List<Event> result = eventRepository.findUpComing();

        assertEquals(1, result.size());
        assertEquals("Concierto", result.get(0).getTitle());
    }

    @Test
    @DisplayName("findUpComing() debe devolver lista vacía cuando no hay eventos futuros")
    void testFindUpComingReturnsEmptyList() {
        when(eventDao.findUpComing()).thenReturn(Collections.emptyList());

        List<Event> result = eventRepository.findUpComing();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findUpComing() debe devolver lista inmutable")
    void testFindUpComingReturnsUnmodifiableList() {
        when(eventDao.findUpComing()).thenReturn(List.of(buildDocument(FAKE_ID)));

        List<Event> result = eventRepository.findUpComing();

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(buildEvent(FAKE_ID)));
    }

    @Test
    @DisplayName("findPast() debe devolver lista de Events mapeados")
    void testFindPastReturnsMappedEvents() {
        when(eventDao.findPast()).thenReturn(List.of(buildDocument(FAKE_ID)));

        List<Event> result = eventRepository.findPast();

        assertEquals(1, result.size());
        assertEquals("Concierto", result.get(0).getTitle());
    }

    @Test
    @DisplayName("findPast() debe devolver lista vacía cuando no hay eventos pasados")
    void testFindPastReturnsEmptyList() {
        when(eventDao.findPast()).thenReturn(Collections.emptyList());

        List<Event> result = eventRepository.findPast();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findPast() debe devolver lista inmutable")
    void testFindPastReturnsUnmodifiableList() {
        when(eventDao.findPast()).thenReturn(List.of(buildDocument(FAKE_ID)));

        List<Event> result = eventRepository.findPast();

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(buildEvent(FAKE_ID)));
    }
}