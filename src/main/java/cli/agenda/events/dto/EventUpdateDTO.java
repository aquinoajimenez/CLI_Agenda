package cli.agenda.events.dto;

import java.time.LocalDateTime;

public record EventUpdateDTO(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location
) {}