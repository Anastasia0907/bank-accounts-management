package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.dao.BankAccountDAO;
import com.anastasia.maryina.banksystem.dao.BankClientDAO;
import com.anastasia.maryina.banksystem.dao.ExchangeRateDAO;
import com.anastasia.maryina.banksystem.dao.TransactionDAO;
import com.anastasia.maryina.banksystem.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class BankAccountServiceImpl implements BankAccountService {

    private static final Scanner sc = new Scanner(System.in);
    private final BankClientDAO bankClientDAO;
    private final BankAccountDAO bankAccountDAO;
    private final TransactionDAO transactionDAO;
    private final ExchangeRateDAO exchangeRateDAO;

    public BankAccountServiceImpl() {
        this.bankClientDAO = new BankClientDAO();
        this.bankAccountDAO = new BankAccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.exchangeRateDAO = new ExchangeRateDAO();
    }

    @Override
    public void createNewAccount(User currentUser, Bank bank) {
        ClientType clientType = chooseClientType();

        BankClient bankClient = bankClientDAO.findByUserBankAndClientType(currentUser.getId(), bank.getId(), clientType)
                .orElseGet(() -> bankClientDAO.save(new BankClient(currentUser, clientType, bank)));

        System.out.print("Enter initial account balance: ");
        BigDecimal initialBalance = sc.nextBigDecimal();
        sc.nextLine();

        Currency currency = chooseCurrency();

        BankAccount bankAccount = new BankAccount(bankClient, currency, initialBalance);
        bankAccount = bankAccountDAO.save(bankAccount);
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, bankAccount, initialBalance);
        transactionDAO.save(transaction);

        System.out.printf("New account created with ID: %s%n", bankAccount.getId());
    }

    private Currency chooseCurrency() {
        System.out.print("Enter account currency (USD, EURO, BYN): ");

        String currencyStr = sc.next();
        try {
            return Currency.valueOf(currencyStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid choice. Try again.");
            return chooseCurrency();
        }
    }

    @Override
    public List<BankAccount> findByUserAndBank(User user, Bank bank) {
        return bankAccountDAO.findByUserAndBank(user, bank);
    }

    @Override
    public Optional<BankAccount> findById(UUID bankAccountId) {
        return bankAccountDAO.findById(bankAccountId);
    }

    @Override
    public List<BankAccount> findByUser(User user) {
        return bankAccountDAO.findByUser(user);
    }


    @Override
    public void makeTransfer(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal transferAmount) {
        boolean sameBankTransfer = sourceAccount.getOwner().getBank().equals(destinationAccount.getOwner().getBank());
        boolean sameCurrencyTransfer = sourceAccount.getCurrency() == destinationAccount.getCurrency();

        BigDecimal commission = BigDecimal.ZERO;
        if (!sameBankTransfer) {
            commission = transferAmount.multiply(sourceAccount.getTransferCommission());
        }

        if (sourceAccount.getTotalAmount().compareTo(transferAmount.add(commission)) < 0) {
            System.out.println("Insufficient funds in the source account.");
            return;
        }

        BigDecimal destinationTransferAmount = transferAmount;
        if (!sameCurrencyTransfer) {
            ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRate(
                    sourceAccount.getOwner().getBank(), sourceAccount.getCurrency(), destinationAccount.getCurrency());
            if (exchangeRate == null) {
                System.out.println("Exchange rate not available for the currency conversion.");
                return;
            }
            destinationTransferAmount = transferAmount.multiply(exchangeRate.getRate());
        }

        Transaction sourceAccountTransaction = new Transaction(TransactionType.CREDIT, sourceAccount, transferAmount, commission);
        sourceAccount.credit(transferAmount.add(commission));
        bankAccountDAO.update(sourceAccount);
        transactionDAO.save(sourceAccountTransaction);

        Transaction destinationAccountTransaction = new Transaction(TransactionType.DEBIT, destinationAccount, destinationTransferAmount);
        destinationAccount.debit(destinationTransferAmount);
        bankAccountDAO.update(destinationAccount);
        transactionDAO.save(destinationAccountTransaction);
    }

    @Override
    public void deposit(BankAccount account, BigDecimal depositAmount) {
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, account, depositAmount);
        account.debit(depositAmount);
        bankAccountDAO.update(account);
        transactionDAO.save(transaction);
    }

    @Override
    public void withdraw(BankAccount account, BigDecimal withdrawalAmount) {
        if (account.getTotalAmount().compareTo(withdrawalAmount) < 0) {
            System.out.println("Insufficient funds in the source account.");
            return;
        }
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, account, withdrawalAmount);
        account.credit(withdrawalAmount);
        bankAccountDAO.update(account);
        transactionDAO.save(transaction);
    }

    private ClientType chooseClientType() {
        System.out.println("Enter client type (1. INDIVIDUAL or 2. LEGAL_ENTITY): ");
        int clientTypeStr = sc.nextInt();
        ClientType clientType;
        switch (clientTypeStr) {
            case 1 -> clientType = ClientType.INDIVIDUAL;
            case 2 -> clientType = ClientType.LEGAL_ENTITY;
            default -> {
                System.out.println("Invalid choice. Try again.");
                clientType = chooseClientType();
            }
        }
        return clientType;
    }

}
