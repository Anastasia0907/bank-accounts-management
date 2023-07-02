package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.dao.BankDAO;
import com.anastasia.maryina.banksystem.dao.ExchangeRateDAO;
import com.anastasia.maryina.banksystem.model.Bank;
import com.anastasia.maryina.banksystem.model.Currency;
import com.anastasia.maryina.banksystem.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class BankServiceImpl implements BankService {

    private static final Scanner sc = new Scanner(System.in);

    private final BankDAO bankDAO;
    private final ExchangeRateDAO exchangeRateDAO;

    public BankServiceImpl() {
        this.bankDAO = new BankDAO();
        this.exchangeRateDAO = new ExchangeRateDAO();
    }

    @Override
    public Bank createNewBank() {
        System.out.println("Enter bank name: ");
        String bankName = sc.nextLine();
        System.out.println("Enter commission for individuals for money transfer to other bank accounts: ");
        BigDecimal individualsTransferCommission = sc.nextBigDecimal();
        System.out.println("Enter commission for legal entities for money transfer to other bank accounts: ");
        BigDecimal legalEntitiesTransferCommission = sc.nextBigDecimal();
        Bank bank = new Bank(bankName, individualsTransferCommission, legalEntitiesTransferCommission);

        System.out.println("Now let's set exchange rates:");
        var currencies = Currency.values();
        var exchangeRates = new ArrayList<ExchangeRate>();
        for (int i = 0; i < currencies.length; i++) {
            for (int j = i + 1; j < currencies.length; j++) {
                System.out.printf("Enter exchange rate from %s to %s: ", currencies[i], currencies[j]);
                BigDecimal rate = sc.nextBigDecimal();
                exchangeRates.add(new ExchangeRate(bank.getId(), currencies[i], currencies[j], rate));
                exchangeRates.add(new ExchangeRate(bank.getId(), currencies[j], currencies[i], BigDecimal.ONE.divide(rate, 4, RoundingMode.HALF_UP)));
            }
        }
        bankDAO.save(bank);
        exchangeRateDAO.saveAll(exchangeRates);
        bank.setExchangeRates(exchangeRates);
        return bank;
    }

    @Override
    public List<Bank> findBanksByUserId(UUID userId) {
        return bankDAO.findBanksByUserId(userId);
    }

    @Override
    public List<Bank> findAll() {
        return bankDAO.findAll();
    }
}
