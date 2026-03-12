package cli.agenda.events.mapper;

import cli.agenda.events.model.Event;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class EventMapper {

    public static Document toDocument(Event event){
        Document document = new Document();

        document.append("title", event.getTitle())
                .append("start_date", parseDate(event.getStartDate()))
                .append("end_date", parseDate(event.getEndDate()))
                .append("created_at", parseDate(event.getCreatedAt()));


        if(event.getId() != null){
            document.append("_id", new ObjectId(event.getId()));
        }
        if(event.getDescription() != null){
            document.append("description", event.getDescription());
        }
        if(event.getLocation() != null){
            document.append("location", event.getLocation());
        }
        if(event.getUpdatedAt() != null){
            document.append("updated_at", parseDate(event.getUpdatedAt()));
        }

        return document;
    }

    public static Event toEvent(Document document){
        return new Event.Builder(
                document.getString("title"),
                parseLocalDateTime(document.getDate("start_date")),
                parseLocalDateTime(document.getDate("end_date"))
        )
                .id(document.getObjectId("_id").toHexString())
                .description(document.getString("description"))
                .location(document.getString("location"))
                .createdAt(parseLocalDateTime(document.getDate("created_at")))
                .updatedAt(parseLocalDateTime(document.getDate("updated_at")))
                .build();
    }

    private static Date parseDate(LocalDateTime localDateTime){
        if(localDateTime == null){
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime parseLocalDateTime(Date date){
        if(date == null){
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
