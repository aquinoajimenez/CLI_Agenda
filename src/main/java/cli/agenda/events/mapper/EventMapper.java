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
        if(document == null) return null;

        Event.Builder builder = new Event.Builder(
                document.getString("title"),
                parseLocalDateTime(document.getDate("start_date")),
                parseLocalDateTime(document.getDate("end_date"))
        );

        ObjectId id = document.getObjectId("_id");
        if(id != null){
            builder.id(id.toHexString());
        }
        if(document.containsKey("description")){
            builder.description(document.getString("description"));
        }
        if(document.containsKey("location")){
            builder.location(document.getString("location"));
        }

        builder.createdAt(parseLocalDateTime(document.getDate("created_at")));

        if(document.containsKey("updated_at")){
            builder.updatedAt(parseLocalDateTime(document.getDate("updated_at")));
        }
        return builder.build();
    }

    public static Event copyWithId(Event event, String id){
        Event.Builder builder = new Event.Builder(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate()
        );

        if(id != null){
            builder.id(id);
        }
        if(event.getDescription() != null){
            builder.description(event.getDescription());
        }
        if(event.getLocation() != null){
            builder.location(event.getLocation());
        }

        builder.createdAt(event.getCreatedAt());

        if(event.getUpdatedAt() != null){
            builder.updatedAt(event.getUpdatedAt());
        }
        return builder.build();
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
