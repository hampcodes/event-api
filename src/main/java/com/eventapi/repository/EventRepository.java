package com.eventapi.repository;

import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    //Listar eventos de una categoría con un estado específico.
    @Query("""
            SELECT DISTINCT ec.event FROM EventCategory ec
            WHERE ec.category.idCategory = :categoryId AND ec.event.status = :status
            """)
    List<Event> findByCategoryAndStatus(@Param("categoryId") Long categoryId,
                                        @Param("status") EventStatus status);

    //buscar un evento solo si tiene boletos suficientes.
    @Query("""
            SELECT e FROM Event e
            WHERE e.id = :eventId AND e.availableTickets >= :quantity
            """)
    Optional<Event> findAvailableEvent(@Param("eventId") Long eventId,
                                       @Param("quantity") Integer quantity);

    //resta boletos disponibles de un evento (UPDATE directo).
    //clearAutomatically limpia el contexto de persistencia para que una lectura posterior (ej. findById) no devuelva el valor cacheado antes del UPDATE.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Event e SET e.availableTickets = e.availableTickets - :quantity
            WHERE e.id = :eventId
            """)
    int decreaseAvailableTickets(@Param("eventId") Long eventId,
                                 @Param("quantity") Integer quantity);


    @Query(value = "SELECT * FROM fn_reporte_ventas_evento(:eventId)",
            nativeQuery = true)
    List<Object[]> reporteVentasPorEvento(@Param("eventId") Long eventId);
}
