package cli.agenda.events.dao;

import org.bson.Document;

public interface EventDao {
    String save(Document document);
}
