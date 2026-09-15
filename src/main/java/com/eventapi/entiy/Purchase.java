package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //En muchos registros de Purchase (Compra) tenemo un solo evento (event_id)
    @JoinColumn(name = "event_id") // FK event_id
    private Event event;

    @ManyToOne
    @JoinColumn(name = "buyer_id") // FK buyer_id
    private User buyer;

    private Integer quantity;
    private BigDecimal totalAmount;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}