package cli.agenda.events.dto;

import java.time.LocalDateTime;

public record EventResponseDTO(
        String id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}