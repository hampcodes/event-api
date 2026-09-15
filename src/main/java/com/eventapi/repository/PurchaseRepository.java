package com.eventapi.repository;

import com.eventapi.entiy.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query(value = """
            SELECT * FROM purchases
            WHERE status = 'PENDING' AND buyer_id = :buyerId
            """, nativeQuery = true)
    List<Purchase> findPendingPurchasesNative(@Param("buyerId") Long buyerId);
}