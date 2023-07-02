package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.dao.TransactionDAO;
import com.anastasia.maryina.banksystem.model.BankAccount;
import com.anastasia.maryina.banksystem.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TransactionServiceImpl implements TransactionService {

    private static final Scanner sc = new Scanner(System.in);

    private final TransactionDAO transactionDAO;

    public TransactionServiceImpl() {
        this.transactionDAO = new TransactionDAO();
    }

    @Override
    public void listTransactions(BankAccount bankAccount) {
        System.out.println("Enter the filter start date (yyyy-MM-dd):");
        String fromDateStr = sc.next();
        LocalDate fromDate = LocalDate.parse(fromDateStr);

        System.out.println("Enter the filter end date (yyyy-MM-dd):");
        String toDateStr = sc.next();
        LocalDate toDate = LocalDate.parse(toDateStr);

        List<Transaction> transactions = transactionDAO.findByAccountAndDateRange(
                bankAccount, fromDate, toDate);

        System.out.printf("Transactions for account: Currency: %s, total amount: %s%n",
                bankAccount.getCurrency(), bankAccount.getTotalAmount());
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}
