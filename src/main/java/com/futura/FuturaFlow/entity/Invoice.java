package com.futura.FuturaFlow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String invoiceNumber; // Номер чека або інвойсу

    @Column(nullable = false)
    private BigDecimal amount; // Сума (BigDecimal ідеально підходить для грошей)

    @Column(name = "percentage")
    private Double percentage;

    @Column(nullable = false)
    private String filePath; // Саме сюди ми запишемо шлях до файлу з нашого FileStorageService

    @Column(nullable = false)
    private String status = "AVAILABLE"; // Початковий статус

    // --- Обов'язковий порожній конструктор для Spring ---
    public Invoice() {}

    // --- Гетери та Сетери ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}