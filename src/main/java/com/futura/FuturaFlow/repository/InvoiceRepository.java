package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Поки що залишаємо порожнім.
    // Базові методи типу save() чи findById() Spring згенерує сам!
}