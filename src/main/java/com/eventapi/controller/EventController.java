package com.eventapi.controller;

import com.eventapi.entiy.Event;
import com.eventapi.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    //Request: GET http://localhost:8080/api/events/1
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id){
        return new ResponseEntity<>(eventService.getEventById(id), HttpStatus.OK);
    }

    //Request: GET http://localhost:8080/api/events
    //ResponseEntity: Transformar Objeto Java o Lista de Objeto Java a formato JSON (JavaScript Object Notation)
    @GetMapping()
    public ResponseEntity <List<Event>> getAllEvents(){
        return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
    }

    //Request: POST http://localhost:8080/api/events
    //ResponseEntity: Transformar Objeto Java o Lista de Objeto Java a formato JSON (JavaScript Object Notation)
    //@RequestBody : Anotacion que se encarga de recibir el objeto JSON y convertirlo a objeto Java
    @PostMapping()
    public ResponseEntity <Event> createEvent(@RequestBody Event event){
        return new ResponseEntity<>(eventService.registerEvent(event), HttpStatus.CREATED);
    }

    //Request: PUT http://localhost:8080/api/events/1
    //ResponseEntity: Transformar Objeto Java o Lista de Objeto Java a formato JSON (JavaScript Object Notation)
    //@RequestBody : Anotacion que se encarga de recibir el objeto JSON y convertirlo a objeto Java
    @PutMapping("/{id}")
    public ResponseEntity <Event> updateEvent(@PathVariable Long id,   @RequestBody Event event){
        return new ResponseEntity<>(eventService.updateEvent(id, event), HttpStatus.OK);
    }


    //Request: DELETE http://localhost:8080/api/events/1
    //ResponseEntity: Transformar Objeto Java o Lista de Objeto Java a formato JSON (JavaScript Object Notation)
    //@RequestBody : Anotacion que se encarga de recibir el objeto JSON y convertirlo a objeto Java
    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
