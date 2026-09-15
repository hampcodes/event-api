package com.eventapi.repository;

import com.eventapi.entiy.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    //Query Method
    List<Payment> findByPurchaseId(Long purchaseId);
}