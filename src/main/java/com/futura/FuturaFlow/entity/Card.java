package com.futura.FuturaFlow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Використовуємо Long, щоб не було проблем як з ФОПом

    private String cardHolder;
    private String cardNumber; // Сюди запишемо повний номер (16 цифр)
    private String expiryDate; // Формат "12/26"

    public Card() {}

    // Гетери та сетери
    public Long getId() { return id; }
    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}