package com.futura.FuturaFlow.enums;

public enum RiskLevel {
    LOW(0.0, 0.0),
    MEDIUM(0.5, 0.05),
    HIGH(1.0, 0.1);

    private final double multiplier;
    private final double premium;

    RiskLevel(double multiplier, double premium) {
        this.multiplier = multiplier;
        this.premium = premium;
    }

    public double getMultiplier() { return multiplier; }
    public double getPremium() { return premium; }
}