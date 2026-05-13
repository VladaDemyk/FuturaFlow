package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Ваші існуючі методи (код команди не зламається)
    Page<Invoice> findByStatus(String status, Pageable pageable);
    List<Invoice> findByAmountBetween(BigDecimal min, BigDecimal max);
    List<Invoice> findByPercentageBetween(Double min, Double max);

    // --- НОВА ТАСКА: Захист від подвійного викупу (Race Condition) ---
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Invoice i WHERE i.id = :id")
    Invoice findByIdWithLock(@Param("id") Long id);
}