package com.futura.FuturaFlow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fops")
public class Fop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Назва ФОПа (наприклад, "ФОП Іваненко")

    @Column(unique = true, nullable = false)
    private String inn; // ІПН (ідентифікаційний код)

    @Column(name = "ipn", length = 10)
    private String ipn; // Ідентифікаційний код (РНОКПП) - критично для YouControl

    @Column(name = "passport_data")
    private String passportData; // Наприклад: "НТ123456 виданий..." - для договору

    @Column(name = "legal_address")
    private String legalAddress; // Юридична адреса - для договору

    // --- Обов'язковий порожній конструктор ---
    public Fop() {}

    // --- Гетери та Сетери ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInn() { return inn; }
    public void setInn(String inn) { this.inn = inn; }
}