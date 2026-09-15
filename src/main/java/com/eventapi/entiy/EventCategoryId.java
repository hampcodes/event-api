package com.eventapi.entiy;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCategoryId implements Serializable {
    private Long eventId;
    private Long categoryId;
}
