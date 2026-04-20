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