package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    private String imageUrl;
    private Integer capacity;
    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
