package events;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;
import cli.agenda.events.model.Event;
import cli.agenda.events.repository.EventRepository;
import cli.agenda.events.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    private EventServiceImpl eventService;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END   = LocalDateTime.now().plusDays(2);
    private static final String FAKE_ID      = "507f1f77bcf86cd799439011";

    private Event existingEvent;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository);
        existingEvent = new Event.Builder("Concierto Original", START, END)
                .id(FAKE_ID)
                .description("Descripción Original")
                .location("Madrid")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Event buildEvent(String id) {
        return new Event.Builder("Concierto", START, END)
                .id(id)
                .description("Descripción")
                .location("Madrid")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create() Crea el evento y devuelve el DTO con ID")
    void testCreateHappyPath() {
        EventCreateDTO dto = new EventCreateDTO(
                "Nuevo Evento", "Desc", START, END, "Barcelona"
        );
        Event savedEvent = new Event.Builder("Nuevo Evento", START, END)
                .id(FAKE_ID)
                .description("Desc")
                .location("Barcelona")
                .createdAt(LocalDateTime.now())
                .build();
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        EventResponseDTO result = eventService.create(dto);

        assertNotNull(result);
        assertEquals(FAKE_ID, result.id());
        assertEquals("Nuevo Evento", result.title());
        assertEquals("Desc", result.description());
        assertEquals("Barcelona", result.location());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("create() Sin campos opcionales devuelve DTO sin descripción ni ubicación")
    void testCreateWithoutOptionalFields() {
        EventCreateDTO dto = new EventCreateDTO(
                "Concierto", null, START, END, null
        );
        Event savedEvent = new Event.Builder("Concierto", START, END)
                .id(FAKE_ID)
                .createdAt(LocalDateTime.now())
                .build();
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        EventResponseDTO result = eventService.create(dto);

        assertNotNull(result);
        assertNull(result.description());
        assertNull(result.location());
    }

    @Test
    @DisplayName("create() Llama al repository exactamente una vez")
    void testCreateCallsRepositoryOnce() {
        EventCreateDTO dto = new EventCreateDTO("Concierto", null, START, END, null);
        when(eventRepository.save(any(Event.class))).thenReturn(buildEvent(FAKE_ID));

        eventService.create(dto);

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("create() Lanza excepción si el título es null")
    void testCreateThrowsExceptionWhenTitleIsNull() {
        EventCreateDTO dto = new EventCreateDTO(null, null, START, END, null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() Lanza excepción si el título está en blanco")
    void testCreateThrowsExceptionWhenTitleIsBlank() {
        EventCreateDTO dto = new EventCreateDTO("  ", null, START, END, null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() Lanza excepción si startDate es null")
    void testCreateThrowsExceptionWhenStartDateIsNull() {
        EventCreateDTO dto = new EventCreateDTO("Concierto", null, null, END, null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() Lanza excepción si endDate es null")
    void testCreateThrowsExceptionWhenEndDateIsNull() {
        EventCreateDTO dto = new EventCreateDTO("Concierto", null, START, null, null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() Lanza excepción si endDate es anterior a startDate")
    void testCreateThrowsExceptionWhenEndDateIsBeforeStartDate() {
        // ✅ Del primer test: verifica que el mensaje contiene el prefijo del Service
        EventCreateDTO dto = new EventCreateDTO("Fallo", "Desc", END, START, "Local");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> eventService.create(dto)
        );

        assertTrue(exception.getMessage().contains("Error al crear el evento"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() Fusión parcial de datos — solo cambia título y ubicación")
    void testUpdateHappyPath() {
        EventUpdateDTO dto = new EventUpdateDTO("Título Cambiado", null, null, null, "Barcelona");
        Event updatedEvent = new Event.Builder("Título Cambiado", START, END)
                .id(FAKE_ID)
                .description("Descripción Original") // mantuvo el viejo
                .location("Barcelona")
                .createdAt(existingEvent.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(eventRepository.findById(FAKE_ID))
                .thenReturn(Optional.of(existingEvent))  // 1ª llamada — verificar si existe
                .thenReturn(Optional.of(updatedEvent));  // 2ª llamada — devolver actualizado
        when(eventRepository.update(eq(FAKE_ID), any(Event.class))).thenReturn(true);

        Optional<EventResponseDTO> result = eventService.update(FAKE_ID, dto);

        assertTrue(result.isPresent());
        assertEquals("Título Cambiado", result.get().title());
        assertEquals("Descripción Original", result.get().description()); // fusión correcta
        assertEquals("Barcelona", result.get().location());
        assertNotNull(result.get().updatedAt());
        verify(eventRepository, times(2)).findById(FAKE_ID);
        verify(eventRepository, times(1)).update(eq(FAKE_ID), any(Event.class));
    }

    @Test
    @DisplayName("update() Todos los campos null mantiene los valores actuales")
    void testUpdateWithAllNullFieldsKeepsCurrentValues() {
        EventUpdateDTO dto = new EventUpdateDTO(null, null, null, null, null);

        when(eventRepository.findById(FAKE_ID))
                .thenReturn(Optional.of(existingEvent))
                .thenReturn(Optional.of(existingEvent));
        when(eventRepository.update(eq(FAKE_ID), any(Event.class))).thenReturn(true);

        Optional<EventResponseDTO> result = eventService.update(FAKE_ID, dto);

        assertTrue(result.isPresent());
        assertEquals("Concierto Original", result.get().title());
        assertEquals("Madrid", result.get().location());
    }

    @Test
    @DisplayName("update() Devuelve Optional.empty() si el ID no existe")
    void testUpdateReturnsEmptyWhenNotFound() {
        EventUpdateDTO dto = new EventUpdateDTO("Algo", "Desc", START, END, "Sitio");
        when(eventRepository.findById(FAKE_ID)).thenReturn(Optional.empty());

        Optional<EventResponseDTO> result = eventService.update(FAKE_ID, dto);

        assertTrue(result.isEmpty());
        verify(eventRepository, never()).update(anyString(), any(Event.class));
    }

    @Test
    @DisplayName("delete() Elimina correctamente y devuelve true")
    void testDeleteHappyPath() {
        when(eventRepository.delete(FAKE_ID)).thenReturn(true);

        boolean result = eventService.delete(FAKE_ID);

        assertTrue(result);
        verify(eventRepository, times(1)).delete(FAKE_ID);
    }

    @Test
    @DisplayName("delete() Devuelve false si el ID no existe")
    void testDeleteReturnsFalseWhenNotFound() {
        when(eventRepository.delete(FAKE_ID)).thenReturn(false);

        boolean result = eventService.delete(FAKE_ID);

        assertFalse(result);
        verify(eventRepository, times(1)).delete(FAKE_ID);
    }

    @Test
    @DisplayName("findUpComing() Transforma lista de Events a DTOs")
    void testFindUpComingHappyPath() {
        when(eventRepository.findUpComing()).thenReturn(List.of(existingEvent));

        List<EventResponseDTO> result = eventService.findUpComing();

        assertEquals(1, result.size());
        assertEquals("Concierto Original", result.get(0).title());
        assertEquals(FAKE_ID, result.get(0).id());
        verify(eventRepository, times(1)).findUpComing();
    }

    @Test
    @DisplayName("findUpComing() Devuelve lista vacía si no hay eventos futuros")
    void testFindUpComingReturnsEmptyList() {
        when(eventRepository.findUpComing()).thenReturn(List.of());

        List<EventResponseDTO> result = eventService.findUpComing();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findUpComing() Devuelve lista inmutable")
    void testFindUpComingReturnsUnmodifiableList() {
        when(eventRepository.findUpComing()).thenReturn(List.of(buildEvent(FAKE_ID)));

        List<EventResponseDTO> result = eventService.findUpComing();

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(new EventResponseDTO(null, null, null, null, null, null, null, null)));
    }

    @Test
    @DisplayName("findPast() Transforma lista de Events a DTOs")
    void testFindPastHappyPath() {
        when(eventRepository.findPast()).thenReturn(List.of(existingEvent));

        List<EventResponseDTO> result = eventService.findPast();

        assertEquals(1, result.size());
        assertEquals("Concierto Original", result.get(0).title());
        verify(eventRepository, times(1)).findPast();
    }

    @Test
    @DisplayName("findPast() Devuelve lista vacía si no hay eventos pasados")
    void testFindPastReturnsEmptyList() {
        when(eventRepository.findPast()).thenReturn(List.of());

        List<EventResponseDTO> result = eventService.findPast();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findPast() Devuelve lista inmutable")
    void testFindPastReturnsUnmodifiableList() {
        when(eventRepository.findPast()).thenReturn(List.of(buildEvent(FAKE_ID)));

        List<EventResponseDTO> result = eventService.findPast();

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(new EventResponseDTO(null, null, null, null, null, null, null, null)));
    }
}
