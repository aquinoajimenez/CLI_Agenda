package cli.agenda.events.dto;

import java.time.LocalDateTime;

public record EventCreateDTO(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location
) {}