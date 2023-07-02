package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.model.BankAccount;

public interface TransactionService {

    void listTransactions(BankAccount bankAccount);
}
