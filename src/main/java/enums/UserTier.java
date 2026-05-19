package com.futura.FuturaFlow.enums;

public enum UserTier {
    STANDARD(1.0),   // Стандартна комісія
    PREMIUM(0.8),    // Знижка 20%
    VIP(0.5);        // Знижка 50%

    private final double discount;

    UserTier(double discount) {
        this.discount = discount;
    }

    public double getDiscount() { return discount; }
}