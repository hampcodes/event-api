package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "events")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private String location;
    private String imageUrl;
    private Integer capacity;
    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @OneToMany(mappedBy = "event")
    private List<EventCategory> eventCategories;

    //Muchos Event puede ser agregar como Favorito por Mucho User
    @ManyToMany(mappedBy = "favoriteEvents")
    private List<User> favoritedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
