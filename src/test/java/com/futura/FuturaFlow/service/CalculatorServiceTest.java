package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.enums.RiskLevel;
import com.futura.FuturaFlow.enums.UserTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    // --- Тест 1: Базовий розрахунок (LOW ризик, STANDARD тариф) ---
    @Test
    void calculateCommissions_lowRisk_standardTier_returnsCorrectValues() {
        double amount = 1000.0;
        int termDays = 365;

        Map<String, Double> result = calculatorService.calculateCommissions(
                amount, termDays, RiskLevel.LOW, UserTier.STANDARD
        );

        // platformFee = 0.015 * (1 + 0.0) * 1.0 = 0.015
        assertEquals(0.015, result.get("platformFee"), 0.0001);

        // investorReturn = 1000 * 0.08 * (1 + 0.0) * (365/365) = 80.0
        assertEquals(80.0, result.get("investorReturn"), 0.0001);

        // fopPayout = amount - platformFee = 1000 - 0.015 = 999.985
        assertEquals(999.985, result.get("fopPayout"), 0.0001);

        // investorTotal = amount + investorReturn = 1000 + 80 = 1080.0
        assertEquals(1080.0, result.get("investorTotal"), 0.0001);
    }

    // --- Тест 2: Середній ризик + PREMIUM тариф (знижка 20%) ---
    @Test
    void calculateCommissions_mediumRisk_premiumTier_appliesDiscount() {
        double amount = 1000.0;
        int termDays = 180;

        Map<String, Double> result = calculatorService.calculateCommissions(
                amount, termDays, RiskLevel.MEDIUM, UserTier.PREMIUM
        );

        // platformFee = 0.015 * (1 + 0.5) * 0.8 = 0.018
        assertEquals(0.018, result.get("platformFee"), 0.0001);

        // investorReturn = 1000 * 0.08 * (1 + 0.05) * (180/365) = 41.42
        double expectedInvestorReturn = 1000 * 0.08 * 1.05 * (180.0 / 365.0);
        assertEquals(expectedInvestorReturn, result.get("investorReturn"), 0.01);
    }

    // --- Тест 3: Високий ризик + VIP тариф (знижка 50%) ---
    @Test
    void calculateCommissions_highRisk_vipTier_appliesMaxDiscount() {
        double amount = 5000.0;
        int termDays = 30;

        Map<String, Double> result = calculatorService.calculateCommissions(
                amount, termDays, RiskLevel.HIGH, UserTier.VIP
        );

        // platformFee = 0.015 * (1 + 1.0) * 0.5 = 0.015
        assertEquals(0.015, result.get("platformFee"), 0.0001);

        // investorReturn = 5000 * 0.08 * (1 + 0.1) * (30/365) = 36.16...
        double expectedInvestorReturn = 5000 * 0.08 * 1.1 * (30.0 / 365.0);
        assertEquals(expectedInvestorReturn, result.get("investorReturn"), 0.01);
    }

    // --- Тест 4: VIP тариф дає меншу комісію ніж STANDARD ---
    @Test
    void calculateCommissions_vipTierFee_isLessThan_standardTierFee() {
        double amount = 2000.0;
        int termDays = 90;

        Map<String, Double> standard = calculatorService.calculateCommissions(
                amount, termDays, RiskLevel.MEDIUM, UserTier.STANDARD
        );
        Map<String, Double> vip = calculatorService.calculateCommissions(
                amount, termDays, RiskLevel.MEDIUM, UserTier.VIP
        );

        assertTrue(vip.get("platformFee") < standard.get("platformFee"),
                "VIP тариф повинен мати меншу комісію ніж STANDARD");
    }

    // --- Тест 5: Більша сума = більший дохід інвестора ---
    @Test
    void calculateCommissions_largerAmount_yieldsHigherInvestorReturn() {
        Map<String, Double> small = calculatorService.calculateCommissions(
                1000.0, 365, RiskLevel.LOW, UserTier.STANDARD
        );
        Map<String, Double> large = calculatorService.calculateCommissions(
                10000.0, 365, RiskLevel.LOW, UserTier.STANDARD
        );

        assertTrue(large.get("investorReturn") > small.get("investorReturn"),
                "Більша сума повинна давати більший дохід інвестора");
    }

    // --- Тест 6: Всі 4 поля присутні у відповіді ---
    @Test
    void calculateCommissions_returnsAllRequiredFields() {
        Map<String, Double> result = calculatorService.calculateCommissions(
                1000.0, 30, RiskLevel.LOW, UserTier.STANDARD
        );

        assertTrue(result.containsKey("platformFee"));
        assertTrue(result.containsKey("investorReturn"));
        assertTrue(result.containsKey("fopPayout"));
        assertTrue(result.containsKey("investorTotal"));
    }
}
