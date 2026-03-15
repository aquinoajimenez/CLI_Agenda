package cli.agenda.events.service;

import cli.agenda.events.dto.EventCreateDTO;
import cli.agenda.events.dto.EventResponseDTO;
import cli.agenda.events.dto.EventUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventResponseDTO create(EventCreateDTO dto);
    Optional<EventResponseDTO> update(String id, EventUpdateDTO dto);
    boolean delete(String id);
    List<EventResponseDTO> findUpComing();
    List<EventResponseDTO> findPast();
}
