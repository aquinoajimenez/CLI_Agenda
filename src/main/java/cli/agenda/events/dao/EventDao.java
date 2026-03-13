package cli.agenda.events.dao;

import org.bson.Document;

import java.util.List;
import java.util.Optional;

public interface EventDao {
    String save(Document document);
    boolean update(String id, Document updates);
    boolean delete(String id);

    Optional<Document> findById(String id);
    List<Document> findUpComing();
    List<Document> findPast();
}
