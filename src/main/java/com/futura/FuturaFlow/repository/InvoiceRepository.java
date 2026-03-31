package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByAmountBetween(BigDecimal min, BigDecimal max);
    List<Invoice> findByPercentageBetween(Double min, Double max);
    // Базові методи типу save() чи findById() Spring згенерує сам!
}