package cli.agenda.events.repository;

import cli.agenda.events.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    boolean update(String id, Event event);
    boolean delete(String id);

    Optional<Event> findById(String id);
    List<Event> findUpComing();
    List<Event> findPast();
}
