package cli.agenda.events.ui;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;
import cli.agenda.events.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EventsMenuCli {

    private final EventService eventService;
    private final Scanner scanner;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EventsMenuCli(EventService eventService, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createEvent();
                case "2" -> listUpcomingEvents();
                case "3" -> listPastEvents();
                case "4" -> updateEvent();
                case "5" -> deleteEvent();
                case "0" -> running = false;
                default  -> System.out.println("❌ Opción no válida.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== MENÚ EVENTOS ===");
        System.out.println("1. ➕ Crear evento");
        System.out.println("2. 📅 Ver eventos próximos");
        System.out.println("3. 🕰️  Ver eventos pasados");
        System.out.println("4. ✏️  Actualizar evento");
        System.out.println("5. 🗑️  Eliminar evento");
        System.out.println("0. ← Volver");
        System.out.print("Elige una opción: ");
    }

    private void createEvent(){
        System.out.println("\n--- CREAR EVENTO ---");

        String title;
        do {
            System.out.print("Título (*): ");
            title = scanner.nextLine().trim();
            if (title.isBlank()) {
                System.out.println("❌ El título es obligatorio. Inténtalo de nuevo.");
            }
        } while (title.isBlank());

        LocalDateTime startDate = null;
        do {
            System.out.print("Fecha inicio (* dd/MM/yyyy HH:mm): ");
            try {
                startDate = LocalDateTime.parse(scanner.nextLine().trim(), FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa: dd/MM/yyyy HH:mm");
            }
        } while (startDate == null);

        LocalDateTime endDate = null;
        do {
            System.out.print("Fecha fin (* dd/MM/yyyy HH:mm): ");
            try {
                endDate = LocalDateTime.parse(scanner.nextLine().trim(), FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Usa: dd/MM/yyyy HH:mm");
            }
        } while (endDate == null);

        System.out.print("Descripción (opcional, Enter para omitir): ");
        String description = scanner.nextLine().trim();

        System.out.print("Ubicación (opcional, Enter para omitir): ");
        String location = scanner.nextLine().trim();

        try {
            EventCreateDTO dto = new EventCreateDTO(
                    title,
                    description.isBlank() ? null : description,
                    startDate,
                    endDate,
                    location.isBlank() ? null : location
            );
            EventResponseDTO created = eventService.create(dto);
            System.out.println("✅ Evento creado correctamente.");
            printEvent(created);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listUpcomingEvents(){
        System.out.println("\n--- EVENTOS PRÓXIMOS ---");

        List<EventResponseDTO> events = eventService.findUpComing();

        if (events.isEmpty()) {
            System.out.println("ℹ️  No hay eventos próximos programados.");
            return;
        }

        System.out.println("Se encontraron " + events.size() + " evento(s):\n");
        events.forEach(this::printEvent);
    }

    private void listPastEvents(){
        System.out.println("\n--- EVENTOS PASADOS ---");

        List<EventResponseDTO> events = eventService.findPast();

        if (events.isEmpty()) {
            System.out.println("ℹ️  No hay eventos pasados registrados.");
            return;
        }

        System.out.println("Se encontraron " + events.size() + " evento(s):\n");
        events.forEach(this::printEvent);
    }

    private void updateEvent(){
        System.out.println("\n--- ACTUALIZAR EVENTO ---");

        String id;
        do {
            System.out.print("ID del evento a actualizar: ");
            id = scanner.nextLine().trim();
            if (id.isBlank()) {
                System.out.println("❌ El ID no puede estar vacío.");
            }
        } while (id.isBlank());

        System.out.println("\nDeja en blanco los campos que no quieras modificar.");

        System.out.print("Nuevo título (Enter para mantener): ");
        String title = scanner.nextLine().trim();

        System.out.print("Nueva fecha inicio (dd/MM/yyyy HH:mm, Enter para mantener): ");
        String startInput = scanner.nextLine().trim();
        LocalDateTime startDate = null;
        if (!startInput.isBlank()) {
            try {
                startDate = LocalDateTime.parse(startInput, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Se mantendrá la fecha actual.");
            }
        }

        System.out.print("Nueva fecha fin (dd/MM/yyyy HH:mm, Enter para mantener): ");
        String endInput = scanner.nextLine().trim();
        LocalDateTime endDate = null;
        if (!endInput.isBlank()) {
            try {
                endDate = LocalDateTime.parse(endInput, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato inválido. Se mantendrá la fecha actual.");
            }
        }

        System.out.print("Nueva descripción (Enter para mantener): ");
        String description = scanner.nextLine().trim();

        System.out.print("Nueva ubicación (Enter para mantener): ");
        String location = scanner.nextLine().trim();

        try {
            EventUpdateDTO dto = new EventUpdateDTO(
                    title.isBlank()       ? null : title,
                    description.isBlank() ? null : description,
                    startDate,
                    endDate,
                    location.isBlank()    ? null : location
            );

            Optional<EventResponseDTO> updated = eventService.update(id, dto);

            if (updated.isPresent()) {
                System.out.println("✅ Evento actualizado correctamente.");
                printEvent(updated.get());
            } else {
                System.out.println("❌ No se encontró un evento con ese ID.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void deleteEvent(){
        System.out.println("\n--- ELIMINAR EVENTO ---");

        String id;
        do {
            System.out.print("ID del evento a eliminar: ");
            id = scanner.nextLine().trim();
            if (id.isBlank()) {
                System.out.println("❌ El ID no puede estar vacío.");
            }
        } while (id.isBlank());

        System.out.print("⚠️  ¿Está seguro de eliminar este evento? (S/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("S")) {
            System.out.println("↩️  Eliminación cancelada.");
            return;
        }

        boolean deleted = eventService.delete(id);

        if (deleted) {
            System.out.println("✅ Evento eliminado correctamente.");
        } else {
            System.out.println("❌ No se encontró un evento con ese ID.");
        }
    }

    private void printEvent(EventResponseDTO event){
        System.out.println("\n  ID        : " + event.id());
        System.out.println("  Título    : " + event.title());
        System.out.println("  Inicio    : " + event.startDate().format(FORMATTER));
        System.out.println("  Fin       : " + event.endDate().format(FORMATTER));
        if (event.description() != null) {
            System.out.println("  Desc.     : " + event.description());
        }
        if (event.location() != null) {
            System.out.println("  Ubicación : " + event.location());
        }
    }
}