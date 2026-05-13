package com.futura.FuturaFlow.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class CalculatorService {

    public Map<String, Double> calculateInvoice(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Сума інвойсу має бути більшою за нуль");

        // Економіка Futura: 1% платформі, 4% інвестору
        double platformFee = amount * 0.01;
        double investorProfit = amount * 0.04;

        // Скільки ФОП отримує прямо зараз
        double fopPayout = amount - platformFee - investorProfit;

        Map<String, Double> result = new HashMap<>();
        result.put("amountTotal", amount);
        result.put("fopGetsNow", fopPayout);
        result.put("investorPaysNow", fopPayout); // Інвестор платить цю суму зараз
        result.put("investorProfit", investorProfit); // А цю забирає як прибуток потім
        result.put("futuraFee", platformFee);

        return result;
    }
}