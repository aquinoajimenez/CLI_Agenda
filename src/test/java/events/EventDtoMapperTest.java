package events;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;
import cli.agenda.events.mapper.EventDtoMapper;
import cli.agenda.events.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventDtoMapperTest {

    private static final LocalDateTime START   = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END     = LocalDateTime.now().plusDays(2);
    private static final LocalDateTime CREATED = LocalDateTime.now();
    private static final LocalDateTime UPDATED = LocalDateTime.now().plusHours(1);

    @Test
    @DisplayName("toCreateEntity() debe mapear todos los campos del EventCreateDTO al Event")
    void toCreateEntityShouldMapAllFields() {
        EventCreateDTO dto = new EventCreateDTO(
                "Reunión", "Hablar de la app", START, END, "Discord"
        );

        Event result = EventDtoMapper.toCreateEntity(dto);

        assertNotNull(result);
        assertEquals("Reunión", result.getTitle());
        assertEquals("Hablar de la app", result.getDescription());
        assertEquals(START, result.getStartDate());
        assertEquals(END, result.getEndDate());
        assertEquals("Discord", result.getLocation());
        assertNotNull(result.getCreatedAt());
        assertNull(result.getId());
    }

    @Test
    @DisplayName("toCreateEntity() debe mapear correctamente sin campos opcionales")
    void toCreateEntityWithoutOptionalFields() {
        EventCreateDTO dto = new EventCreateDTO(
                "Reunión Rápida", null, START, END, null
        );

        Event result = EventDtoMapper.toCreateEntity(dto);

        assertEquals("Reunión Rápida", result.getTitle());
        assertNull(result.getDescription());
        assertNull(result.getLocation());
    }

    @Test
    @DisplayName("toUpdateEntity() debe mapear todos los campos del EventUpdateDTO al Event")
    void toUpdateEntityShouldMapAllFields() {
        EventUpdateDTO dto = new EventUpdateDTO(
                "Reunión Modificada", "Nueva descripción", START, END, "Skype"
        );

        Event result = EventDtoMapper.toUpdateEntity(dto);

        assertNotNull(result);
        assertEquals("Reunión Modificada", result.getTitle());
        assertEquals("Nueva descripción", result.getDescription());
        assertEquals(START, result.getStartDate());
        assertEquals(END, result.getEndDate());
        assertEquals("Skype", result.getLocation());
    }

    @Test
    @DisplayName("toResponseDTO() debe mapear todos los campos del Event al EventResponseDTO")
    void toResponseDTOShouldMapAllFields() {
        Event event = new Event.Builder("Concierto", START, END)
                .id("507f1f77bcf86cd799439011")
                .description("Música en vivo")
                .location("Wizink Center")
                .createdAt(CREATED)
                .updatedAt(UPDATED)
                .build();

        EventResponseDTO result = EventDtoMapper.toResponseDTO(event);

        assertNotNull(result);
        assertEquals("507f1f77bcf86cd799439011", result.id());
        assertEquals("Concierto", result.title());
        assertEquals("Música en vivo", result.description());
        assertEquals(START, result.startDate());
        assertEquals(END, result.endDate());
        assertEquals("Wizink Center", result.location());
        assertEquals(CREATED, result.createdAt());
        assertEquals(UPDATED, result.updatedAt());
    }

    @Test
    @DisplayName("toResponseDTO() debe mapear correctamente sin campos opcionales")
    void toResponseDTOWithoutOptionalFields() {
        Event event = new Event.Builder("Concierto", START, END)
                .id("507f1f77bcf86cd799439011")
                .createdAt(CREATED)
                .build();

        EventResponseDTO result = EventDtoMapper.toResponseDTO(event);

        assertNull(result.description());
        assertNull(result.location());
        assertNull(result.updatedAt());
        assertNotNull(result.id());
        assertNotNull(result.createdAt());
    }
}
