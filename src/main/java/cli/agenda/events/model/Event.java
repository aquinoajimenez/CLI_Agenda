package cli.agenda.events.model;

import java.time.LocalDateTime;

public class Event {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Event(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.location = builder.location;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(String title, String description, LocalDateTime startDate, LocalDateTime endDate, String location){
        if(title != null && !title.isBlank()) this.title = title;
        if(description != null && !description.isBlank()) this.description = description;
        if(startDate != null) this.startDate = startDate;
        if(endDate != null ) this.endDate = endDate;
        if(location != null && !location.isBlank()) this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public static class Builder{
        private String id;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String location;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder(String title, LocalDateTime startDate, LocalDateTime endDate) {
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.createdAt = LocalDateTime.now();
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder location(String location){
            this.location = location;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt){
            this.updatedAt = updatedAt;
            return this;
        }

        public Event build(){

            if(title == null || title.isBlank()){
                throw new IllegalArgumentException("El título es obligatorio");
            }
            if(startDate == null){
                throw new IllegalArgumentException("La fecha de inicio es obligatoria");
            }
            if(endDate == null){
                throw new IllegalArgumentException("La fecha de fin es obligatoria");
            }
            if(endDate.isBefore(startDate)){
                throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
            }
            return new Event(this);
        }
    }
}
