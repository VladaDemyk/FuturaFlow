package com.futura.FuturaFlow.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import com.futura.FuturaFlow.enums.RiskLevel;
import com.futura.FuturaFlow.enums.UserTier;
import java.util.Map;
@Service
public class CalculatorService {

    // Базові ставки
    private static final double BASE_PLATFORM_FEE = 0.015;  // 1.5%
    private static final double BASE_INVESTOR_RETURN = 0.08; // 8% річних

    public Map<String, Double> calculateCommissions(
            Double amount,
            Integer termDays,
            RiskLevel riskLevel,
            UserTier userTier) {

        // Платформі: 1.5-3% залежно від ризику
        double platformFee = BASE_PLATFORM_FEE *
                (1 + riskLevel.getMultiplier()) *
                userTier.getDiscount();

        // Інвестору: 8-15% річних, перерахунок на термін
        double annualRate = BASE_INVESTOR_RETURN * (1 + riskLevel.getPremium());
        double investorReturn = amount * annualRate * (termDays / 365.0);

        return Map.of(
                "platformFee", platformFee,
                "investorReturn", investorReturn,
                "fopPayout", amount - platformFee,
                "investorTotal", amount + investorReturn
        );
    }
}
