package com.futura.FuturaFlow.repository;


import com.futura.FuturaFlow.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Для історії (з пагінацією та фільтром)
    Page<Transaction> findByType(String type, Pageable pageable);

    // Для Summary (рахує суму за типом)
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") String type);
}