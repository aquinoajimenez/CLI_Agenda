package cli.agenda.events.repository;

import cli.agenda.events.dao.EventDao;
import cli.agenda.events.mapper.EventMapper;
import cli.agenda.events.model.Event;
import org.bson.Document;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventRepositoryImpl implements EventRepository {
    private final EventDao eventDao;

    public EventRepositoryImpl(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public Event save(Event event) {
        Document document = EventMapper.toDocument(event);
        String generatedId = eventDao.save(document);

        return EventMapper.copyWithId(event, generatedId);
    }

    @Override
    public boolean update(String id, Event event) {
        Document document = EventMapper.toDocument(event);
        document.remove("_id");
        return eventDao.update(id, document);
    }

    @Override
    public boolean delete(String id) {
        return eventDao.delete(id);
    }

    @Override
    public Optional<Event> findById(String id) {
        return eventDao.findById(id).map(document -> EventMapper.toEvent(document));
    }

    @Override
    public List<Event> findUpComing() {
        return eventDao.findUpComing().stream().map(document -> EventMapper.toEvent(document)).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Event> findPast() {
        return eventDao.findPast().stream().map(document -> EventMapper.toEvent(document)).collect(Collectors.toUnmodifiableList());
    }
}