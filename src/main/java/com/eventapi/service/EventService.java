package com.eventapi.service;

import com.eventapi.entiy.Event;
import com.eventapi.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id){
        return eventRepository.findById(id).get();
    }

    public Event registerEvent(Event event){
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event event){
        Event eventExist = eventRepository.findById(id).get();
        eventExist.setName(event.getName());
        eventExist.setDescription(event.getDescription());
        eventExist.setDate(event.getDate());
        eventExist.setLocation(event.getLocation());
        eventExist.setCategory(event.getCategory());
        eventExist.setImageUrl(event.getImageUrl());
        eventExist.setCapacity(event.getCapacity());
        eventExist.setAvailableTickets(event.getAvailableTickets());
        eventExist.setPrice(event.getPrice());
        eventExist.setStatus(event.getStatus());
        eventExist.setCreatedAt(event.getCreatedAt());
        eventExist.setUpdatedAt(event.getUpdatedAt());
        return eventRepository.save(eventExist);
    }

    public void deleteEvent(Long id){
        eventRepository.deleteById(id);
    }
}
