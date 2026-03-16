package cli.agenda.events.service;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;
import cli.agenda.events.mapper.EventDtoMapper;
import cli.agenda.events.model.Event;
import cli.agenda.events.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventResponseDTO create(EventCreateDTO dto) {
        try{
           Event event = EventDtoMapper.toCreateEntity(dto);
           Event saved = eventRepository.save(event);
           return EventDtoMapper.toResponseDTO(saved);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Error al crear el evento: " + e.getMessage());
        }
    }

    @Override
    public Optional<EventResponseDTO> update(String id, EventUpdateDTO dto) {
        Optional<Event> existing = eventRepository.findById(id);
        if(existing.isEmpty()){
            return Optional.empty();
        }
        Event current = existing.get();

        String title = dto.title() != null ? dto.title() : current.getTitle();
        String description = dto.description() != null ? dto.description() : current.getDescription();
        String location = dto.location() != null ? dto.location() : current.getLocation();
        LocalDateTime startDate = dto.startDate() != null ? dto.startDate() : current.getStartDate();
        LocalDateTime endDate = dto.endDate() != null ? dto.endDate() : current.getEndDate();

        Event updated = new Event.Builder(title, startDate, endDate)
                .description(description)
                .location(location)
                .createdAt(current.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        eventRepository.update(id, updated);

        return eventRepository.findById(id).map(event -> EventDtoMapper.toResponseDTO(event));
    }

    @Override
    public boolean delete(String id) {
        return eventRepository.delete(id);
    }

    @Override
    public List<EventResponseDTO> findUpComing() {
        return eventRepository.findUpComing().stream()
                .map(event -> EventDtoMapper.toResponseDTO(event))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<EventResponseDTO> findPast() {
        return eventRepository.findPast().stream()
                .map(event -> EventDtoMapper.toResponseDTO(event))
                .collect(Collectors.toUnmodifiableList());
    }
}
