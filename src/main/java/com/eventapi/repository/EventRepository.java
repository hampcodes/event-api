package com.eventapi.repository;

import com.eventapi.entiy.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
