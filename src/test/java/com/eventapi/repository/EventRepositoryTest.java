package com.eventapi.repository;

import com.eventapi.entiy.Category;
import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventStatus;
import com.eventapi.entiy.Purchase;
import com.eventapi.entiy.PurchaseStatus;
import com.eventapi.entiy.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventRepositoryTest {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    @Test
    void findByCategoryAndStatus_shouldReturnEventMatchingCategoryAndStatus() {
        System.out.println(">>> Probando: listar eventos de una categoría que además tengan un status específico.");

        Event activeEvent = new Event();
        activeEvent.setName("Concierto Activo");
        activeEvent.setDescription("Descripción de prueba");
        activeEvent.setDate(LocalDateTime.now().plusDays(5));
        activeEvent.setLocation("Lugar de prueba");
        activeEvent.setImageUrl("https://example.com/image.jpg");
        activeEvent.setCapacity(100);
        activeEvent.setAvailableTickets(50);
        activeEvent.setPrice(new BigDecimal("30.00"));
        activeEvent.setStatus(EventStatus.ACTIVE);
        activeEvent.setCreatedAt(LocalDateTime.now());
        activeEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(activeEvent);

        Event canceledEvent = new Event();
        canceledEvent.setName("Concierto Cancelado");
        canceledEvent.setDescription("Descripción de prueba");
        canceledEvent.setDate(LocalDateTime.now().plusDays(5));
        canceledEvent.setLocation("Lugar de prueba");
        canceledEvent.setImageUrl("https://example.com/image.jpg");
        canceledEvent.setCapacity(100);
        canceledEvent.setAvailableTickets(50);
        canceledEvent.setPrice(new BigDecimal("30.00"));
        canceledEvent.setStatus(EventStatus.CANCELED);
        canceledEvent.setCreatedAt(LocalDateTime.now());
        canceledEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(canceledEvent);

        Category category = categoryRepository.save(new Category(null, "Rock"));

        EventCategory activeLink = new EventCategory();
        activeLink.setEvent(activeEvent);
        activeLink.setCategory(category);
        activeLink.setIsPrimary(true);
        eventCategoryRepository.save(activeLink);

        EventCategory canceledLink = new EventCategory();
        canceledLink.setEvent(canceledEvent);
        canceledLink.setCategory(category);
        canceledLink.setIsPrimary(true);
        eventCategoryRepository.save(canceledLink);

        List<Event> found = eventRepository.findByCategoryAndStatus(
                category.getIdCategory(), EventStatus.ACTIVE);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo(activeEvent.getId());
    }

    @Test
    void findAvailableEvent_shouldReturnEvent_whenEnoughTickets() {
        System.out.println(">>> Probando: buscar un evento por id solo si tiene boletos suficientes (hay cupo).");

        Event event = new Event();
        event.setName("Evento con cupo");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(10);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Optional<Event> found = eventRepository.findAvailableEvent(event.getId(), 5);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).isPresent();
    }

    @Test
    void findAvailableEvent_shouldReturnEmpty_whenNotEnoughTickets() {
        System.out.println(">>> Probando: la misma búsqueda de cupo, pero pidiendo más boletos de los disponibles (no debe devolver nada).");

        Event event = new Event();
        event.setName("Evento sin cupo");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(2);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Optional<Event> found = eventRepository.findAvailableEvent(event.getId(), 5);

        System.out.println(">>> Resultado: " + found);

        assertThat(found).isEmpty();
    }

    @Test
    void decreaseAvailableTickets_shouldSubtractQuantity() {
        System.out.println(">>> Probando: el UPDATE que descuenta boletos disponibles de un evento.");

        Event event = new Event();
        event.setName("Evento para descontar");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(10);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        int updatedRows = eventRepository.decreaseAvailableTickets(event.getId(), 3);
        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();

        System.out.println(">>> Resultado: filas actualizadas=" + updatedRows
                + ", boletos restantes=" + reloaded.getAvailableTickets());

        assertThat(updatedRows).isEqualTo(1);
        assertThat(reloaded.getAvailableTickets()).isEqualTo(7);
    }

    // Requiere que la función fn_reporte_ventas_evento ya exista en la BD (ver Paso 16 de la guía).
    @Test
    void reporteVentasPorEvento_shouldReturnSalesSummary() {
        System.out.println(">>> Probando: el reporte de ventas de un evento (función almacenada nativa fn_reporte_ventas_evento).");

        Event event = new Event();
        event.setName("Evento con ventas");
        event.setDescription("Descripción de prueba");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setLocation("Lugar de prueba");
        event.setImageUrl("https://example.com/image.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(50);
        event.setPrice(new BigDecimal("30.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        User buyer = new User();
        buyer.setUsername("buyer_reporte_" + System.nanoTime());
        buyer.setSupabaseUserId("sb-uuid-test");
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(2);
        purchase.setTotalAmount(new BigDecimal("60.00"));
        purchase.setCode("RPT-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        List<Object[]> reporte = eventRepository.reporteVentasPorEvento(event.getId());

        reporte.forEach(fila -> System.out.println(">>> Resultado: " + java.util.Arrays.toString(fila)));

        assertThat(reporte).isNotEmpty();
    }
}
