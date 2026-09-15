package com.eventapi.repository;

import com.eventapi.entiy.Event;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PurchaseRepositoryTest {

    private final PurchaseRepository purchaseRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Test
    void findPendingPurchasesNative_shouldReturnOnlyPendingPurchasesForBuyer() {
        System.out.println(">>> Probando: SQL nativo que trae solo las compras PENDING de un comprador.");

        User buyer = new User();
        buyer.setUsername("buyer_pending_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento con compras");
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

        Purchase pendingPurchase = new Purchase();
        pendingPurchase.setEvent(event);
        pendingPurchase.setBuyer(buyer);
        pendingPurchase.setQuantity(1);
        pendingPurchase.setTotalAmount(new BigDecimal("30.00"));
        pendingPurchase.setCode("PEND-" + System.nanoTime());
        pendingPurchase.setStatus(PurchaseStatus.PENDING);
        pendingPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(pendingPurchase);

        Purchase paidPurchase = new Purchase();
        paidPurchase.setEvent(event);
        paidPurchase.setBuyer(buyer);
        paidPurchase.setQuantity(1);
        paidPurchase.setTotalAmount(new BigDecimal("30.00"));
        paidPurchase.setCode("PAID-" + System.nanoTime());
        paidPurchase.setStatus(PurchaseStatus.PAID);
        paidPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(paidPurchase);

        List<Purchase> pending = purchaseRepository.findPendingPurchasesNative(buyer.getIdUser());

        System.out.println(">>> Resultado: " + pending);

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getStatus()).isEqualTo(PurchaseStatus.PENDING);
    }

    @Test
    void findPendingPurchasesNative_shouldReturnEmpty_whenBuyerHasNoPendingPurchases() {
        System.out.println(">>> Probando: el mismo SQL nativo, pero cuando el comprador no tiene ninguna compra PENDING.");

        User buyer = new User();
        buyer.setUsername("buyer_nopending_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento sin pendientes");
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

        Purchase paidPurchase = new Purchase();
        paidPurchase.setEvent(event);
        paidPurchase.setBuyer(buyer);
        paidPurchase.setQuantity(1);
        paidPurchase.setTotalAmount(new BigDecimal("30.00"));
        paidPurchase.setCode("PAID-" + System.nanoTime());
        paidPurchase.setStatus(PurchaseStatus.PAID);
        paidPurchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(paidPurchase);

        List<Purchase> pending = purchaseRepository.findPendingPurchasesNative(buyer.getIdUser());

        System.out.println(">>> Resultado: " + pending);

        assertThat(pending).isEmpty();
    }
}
