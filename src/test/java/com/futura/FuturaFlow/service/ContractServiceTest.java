package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContractServiceTest {

    private ContractService contractService;
    private User fop;
    private User investor;
    private Invoice invoice;
    private Map<String, Double> calculations;

    @BeforeEach
    void setUp() {
        contractService = new ContractService();

        fop = new User();
        fop.setUsername("Василь");
        fop.setLastName("Дмитренко");
        fop.setIpn("3216549870");
        fop.setPassportData("НТ123456, виданий 12.05.2015");
        fop.setLegalAddress("м. Львів, вул. Бандери, 12");

        investor = new User();
        investor.setUsername("Олег");
        investor.setLastName("Коваль");
        investor.setIpn("1234567890");

        invoice = new Invoice();
        invoice.setId(42L);
        invoice.setInvoiceNumber("INV-001");

        calculations = Map.of(
                "amountTotal", 10000.0,
                "fopGetsNow", 9700.0,
                "futuraFee", 150.0,
                "investorProfit", 300.0
        );
    }

    // --- Тест 1: Договір містить номер у правильному форматі ---
    @Test
    void generateContract_containsContractNumber() {
        String contract = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        assertTrue(contract.contains("FUTR-"), "Договір повинен містити номер у форматі FUTR-XXXXXXXX");
    }

    // --- Тест 2: Договір містить дані ФОП ---
    @Test
    void generateContract_containsFopData() {
        String contract = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        assertTrue(contract.contains("Дмитренко"), "Договір повинен містити прізвище ФОП");
        assertTrue(contract.contains("3216549870"), "Договір повинен містити ІПН ФОП");
        assertTrue(contract.contains("м. Львів"), "Договір повинен містити адресу ФОП");
    }

    // --- Тест 3: Договір містить дані інвестора ---
    @Test
    void generateContract_containsInvestorData() {
        String contract = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        assertTrue(contract.contains("Коваль"), "Договір повинен містити прізвище інвестора");
        assertTrue(contract.contains("1234567890"), "Договір повинен містити ІПН інвестора");
    }

    // --- Тест 4: Договір містить фінансові розрахунки ---
    @Test
    void generateContract_containsCalculations() {
        String contract = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        assertTrue(contract.contains("10000.0"), "Договір повинен містити загальну суму");
        assertTrue(contract.contains("9700.0"), "Договір повинен містити суму для ФОП");
        assertTrue(contract.contains("150.0"), "Договір повинен містити комісію платформи");
        assertTrue(contract.contains("300.0"), "Договір повинен містити прибуток інвестора");
    }

    // --- Тест 5: Кожен виклик генерує унікальний номер договору ---
    @Test
    void generateContract_eachCallProducesUniqueContractNumber() {
        String contract1 = contractService.generateInvoiceContract(fop, investor, invoice, calculations);
        String contract2 = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        // Витягуємо номери договорів
        String number1 = contract1.lines()
                .filter(l -> l.contains("FUTR-"))
                .findFirst().orElse("");
        String number2 = contract2.lines()
                .filter(l -> l.contains("FUTR-"))
                .findFirst().orElse("");

        assertNotEquals(number1, number2, "Кожен договір повинен мати унікальний номер");
    }

    // --- Тест 6: Договір містить статус підписання ---
    @Test
    void generateContract_containsSigningStatus() {
        String contract = contractService.generateInvoiceContract(fop, investor, invoice, calculations);

        assertTrue(contract.contains("Дія.Підпис"), "Договір повинен містити статус підписання");
    }
}
