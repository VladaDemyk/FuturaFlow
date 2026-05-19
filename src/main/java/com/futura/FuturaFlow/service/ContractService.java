package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.entity.Invoice;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ContractService {

    public String generateInvoiceContract(User fop, User investor, Invoice invoice, Map<String, Double> calculations) {
        String contractNumber = "FUTR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "ДОГОВІР ВІДСТУПЛЕННЯ ПРАВА ВИМОГИ (ЦЕСІЇ) № " + contractNumber + "\n" +
                "м. Львів, Україна\n\n" +
                "Цей Договір укладено між сторонами платформи Futura:\n" +
                "1. ПЕРВІСНИЙ КРЕДИТОР: ФОП " + fop.getLastName() + " " + fop.getUsername() + ",\n" +
                "   ІПН: " + fop.getIpn() + ",\n" +
                "   Паспортні дані: " + fop.getPassportData() + ",\n" +
                "   Юридична адреса: " + fop.getLegalAddress() + "\n\n" +
                "2. НОВИЙ КРЕДИТОР (ІНВЕСТОР): " + investor.getLastName() + " " + investor.getUsername() + ",\n" +
                "   ІПН: " + investor.getIpn() + "\n\n" +
                "1. ПРЕДМЕТ ДОГОВОРУ\n" +
                "1.1. Первісний кредитор відступає Нового кредитору повне право вимоги грошових коштів за Інвойсом №" + invoice.getId() + ".\n" +
                "1.2. Загальна сума вимоги за Інвойсом становить: " + calculations.get("amountTotal") + " грн.\n" +
                "1.3. Новий кредитор зобов'язується сплатити на рахунок Первісного кредитора суму у розмірі: " + calculations.get("fopGetsNow") + " грн.\n" +
                "1.4. Комісія платформи Futura становить: " + calculations.get("futuraFee") + " грн.\n" +
                "1.5. Чистий прибуток Інвестора за фактом закриття інвойсу дебітором становить: " + calculations.get("investorProfit") + " грн.\n\n" +
                "2. ПОРЯДОК ПІДПИСАННЯ\n" +
                "2.1. Цей договір вважається укладеним з моменту накладення Електронних Цифрових Підписів (КЕП) обома Сторонами на платформі Futura.\n\n" +
                "СТОРОНИ ОЗНАЙОМЛЕНІ ТА ПОГОДЖУЮТЬСЯ З УМОВАМИ:\n" +
                "[ СТАТУС: Очікує на підписання через Дія.Підпис ]";
    }
}