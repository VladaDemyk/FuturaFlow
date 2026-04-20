package com.futura.FuturaFlow.dto;
import com.futura.FuturaFlow.entity.Card;

public class CardDTO {
    private Long id;
    private String cardHolder;
    private String maskedNumber; // Оце те, що побачить користувач
    private String expiryDate;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.cardHolder = card.getCardHolder();
        this.expiryDate = card.getExpiryDate();

        // Маскування: беремо останні 4 цифри
        String fullNumber = card.getCardNumber();
        if (fullNumber != null && fullNumber.length() >= 4) {
            this.maskedNumber = "**** **** **** " + fullNumber.substring(fullNumber.length() - 4);
        } else {
            this.maskedNumber = "Invalid Card";
        }
    }

    // Тільки гетери (сетери тут не потрібні)
    public Long getId() { return id; }
    public String getCardHolder() { return cardHolder; }
    public String getMaskedNumber() { return maskedNumber; }
    public String getExpiryDate() { return expiryDate; }
}