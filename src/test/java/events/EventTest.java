package events;

import cli.agenda.events.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    @DisplayName("El test debe crear un Event completo con todos sus campos")
    void createEventWithAllFields(){
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Event event = new Event.Builder("Fin de semana en Mallorca", start, end)
                .description("Vamos a ir de playas y a tomar el sol")
                .location("Mallorca")
                .id("12345abcde")
                .updatedAt(start)
                .build();

        assertAll("Comprobando todos los atributos del evento",
                () -> assertEquals("Fin de semana en Mallorca", event.getTitle()),
                () -> assertEquals(start, event.getStartDate()),
                () -> assertEquals(end, event.getEndDate()),
                () -> assertEquals("12345abcde", event.getId()),
                () -> assertEquals("Vamos a ir de playas y a tomar el sol", event.getDescription()),
                () -> assertEquals("Mallorca", event.getLocation()),
                () -> assertNotNull(event.getCreatedAt(), "El createdAt debería autogenerarse"),
                () -> assertEquals(start, event.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Solamente creamos un evento con sus atributos obligatorios")
    void CreateEventWithOnlyRequiredFields(){
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Event event = new Event.Builder("Fin de semana en Mallorca", start, end).build();

        assertAll("Comprobando todos los atributos del evento",
                () -> assertEquals("Fin de semana en Mallorca", event.getTitle()),
                () -> assertEquals(start, event.getStartDate()),
                () -> assertEquals(end, event.getEndDate()),

                () -> assertNull(event.getDescription(), "La descripción debe ser nula"),
                () -> assertNull(event.getLocation(), "La ubicación debe ser nula"),
                () -> assertNull(event.getId(), "El ID debe ser nulo"),
                () -> assertNull(event.getUpdatedAt(), "El updatedAt debe ser nulo")
        );
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando no se introducen ninguno de los 3 atributos obligatorios")
    void throwExceptionWhenMandatoryFieldsAreNull(){

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder(null, null, null).build();
        }, "Debe de saltar la excepcion si los atributos son nulos");
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando se introduce el titulo en blanco")
    void throwExceptionWhenTitleIsInBlank(){

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder("", start, end).build();
        }, "Debe de saltar la excepcion si el titulo esta en blanco");
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando solo se introduce el titulo en nulo")
    void throwExceptionsWhenTitleIsNull(){

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder(null, start, end).build();
        }, "Debe de saltar la excepcion si el título es nulo");
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando solo se introduce startDate en nulo")
    void throwExceptionWhenStartDateIsNull(){

        LocalDateTime end = LocalDateTime.now().plusDays(3);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder("Vacaciones", null, end).build();
        }, "Debe de saltar la excepcion si startDate es nulo");
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando solo se introduce el endDate en nulo")
    void throwExceptionsWhenEndDateIsNull(){

        LocalDateTime start = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder("Vacaciones", start, null).build();
        }, "Debe de saltar la excepcion si el endDate es nulo");
    }

    @Test
    @DisplayName("Se comprueba que salta la excepcion cuando endDate es antes que startDate")
    void throwExceptionWhenEndDateIsBeforeStartDate(){

        LocalDateTime end = LocalDateTime.now().plusDays(1);
        LocalDateTime start = end.plusDays(3);

        assertThrows(IllegalArgumentException.class, () -> {
            new Event.Builder("Vacaciones", start, end).build();
        }, "Debe de saltar la excepcion si endDate va antes que startDate");
    }

    @Test
    @DisplayName("Se comprueba si update funciona correctamente")
    void updateEvent(){
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);

        Event event = new Event.Builder("Partido de futbol", start, end)
                .description("Contra el Real Madrid")
                .location("Madrid")
                .build();

        event.update("Tenis", "Rafa Nadal", null, null, "Mallorca");

        assertAll(
                () -> assertEquals("Tenis", event.getTitle()),
                () -> assertEquals("Rafa Nadal", event.getDescription()),
                () -> assertEquals("Mallorca", event.getLocation()),
                () -> assertNotNull(event.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Se comprueba si no se cambia el titulo cuando esta en blanco al usar update")
    void updateEventWithTitleInBlank(){
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);

        Event event = new Event.Builder("Partido de futbol", start, end)
                .description("Contra el Real Madrid")
                .location("Madrid")
                .build();

        event.update("", null, null, null, null);

        assertEquals("Partido de futbol", event.getTitle());
    }

    @Test
    @DisplayName("Se comprueba si no se cambia el titulo cuando esta en nulo al usar update")
    void updateEventWithTitleIsNull(){
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);

        Event event = new Event.Builder("Partido de futbol", start, end)
                .description("Contra el Real Madrid")
                .location("Madrid")
                .build();

        event.update(null, null, null, null, null);

        assertEquals("Partido de futbol", event.getTitle());
    }

    @Test
    @DisplayName("startDate igual a endDate debería ser válido")
    void endDateEqualToStartDateShouldBeValid(){
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        assertDoesNotThrow(() -> new Event.Builder("Evento", date, date).build());
    }
}
