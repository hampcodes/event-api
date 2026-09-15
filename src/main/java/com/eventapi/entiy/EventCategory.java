package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCategory {

    @EmbeddedId// PK Compuesto
    private EventCategoryId id = new EventCategoryId();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")//FK
    private Event event;//Ref Entidad Event

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")//FK
    private Category category;//Ref Entidad Category

    @Column(name = "is_primary")
    private Boolean isPrimary;

}
