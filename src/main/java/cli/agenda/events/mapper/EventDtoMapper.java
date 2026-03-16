package cli.agenda.events.mapper;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;
import cli.agenda.events.model.Event;

public class EventDtoMapper {

    public static Event toCreateEntity(EventCreateDTO dto){
        return new Event.Builder(dto.title(), dto.startDate(), dto.endDate())
                .description(dto.description())
                .location(dto.location())
                .build();
    }

    public static Event toUpdateEntity(EventUpdateDTO dto){
        return  new Event.Builder(dto.title(), dto.startDate(), dto.endDate())
                .description(dto.description())
                .location(dto.location())
                .build();
    }

    public static EventResponseDTO toResponseDTO(Event event){
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
