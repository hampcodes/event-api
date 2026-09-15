package com.eventapi.repository;

import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventStatus;
import com.eventapi.entiy.Payment;
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
class PaymentRepositoryTest {

    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Test
    void findByPurchaseId_shouldReturnPaymentsOfThatPurchase() {
        System.out.println(">>> Probando: Query Method que trae los pagos asociados a una compra.");

        User buyer = new User();
        buyer.setUsername("buyer_payment_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento con pago");
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

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(1);
        purchase.setTotalAmount(new BigDecimal("30.00"));
        purchase.setCode("PAY-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setMethod("YAPE");
        payment.setAmount(new BigDecimal("30.00"));
        payment.setStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByPurchaseId(purchase.getId());

        System.out.println(">>> Resultado: " + payments);

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getMethod()).isEqualTo("YAPE");
    }

    @Test
    void findByPurchaseId_shouldReturnEmpty_whenPurchaseHasNoPayments() {
        System.out.println(">>> Probando: la misma query cuando la compra todavía no tiene ningún pago registrado.");

        User buyer = new User();
        buyer.setUsername("buyer_nopayment_" + System.nanoTime());
        buyer.setSupabaseUserId(UUID.randomUUID().toString());
        buyer.setEnabled(true);
        userRepository.save(buyer);

        Event event = new Event();
        event.setName("Evento sin pago");
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

        Purchase purchase = new Purchase();
        purchase.setEvent(event);
        purchase.setBuyer(buyer);
        purchase.setQuantity(1);
        purchase.setTotalAmount(new BigDecimal("30.00"));
        purchase.setCode("NOPAY-" + System.nanoTime());
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setCreatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);

        List<Payment> payments = paymentRepository.findByPurchaseId(purchase.getId());

        System.out.println(">>> Resultado: " + payments);

        assertThat(payments).isEmpty();
    }
}
