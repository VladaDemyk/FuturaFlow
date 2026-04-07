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
    private String status;
    @Column(nullable = false)
    private String filePath; 
    @Column(nullable = true) // Дозволяємо бути порожнім, бо для PDF прев'ю ми не генеруємо
    private String thumbnailPath;
    // --- Обов'язковий порожній конструктор для Spring ---
    public Invoice() {}

    // --- Гетери та Сетери ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    public String getStatus() { return status;}
    public void setStatus(String status) { this.status = status;}
}