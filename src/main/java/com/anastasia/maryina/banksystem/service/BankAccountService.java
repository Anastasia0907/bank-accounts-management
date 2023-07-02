package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.model.Bank;
import com.anastasia.maryina.banksystem.model.BankAccount;
import com.anastasia.maryina.banksystem.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountService {

    void createNewAccount(User currentUser, Bank bank);

    List<BankAccount> findByUserAndBank(User user, Bank bank);

    Optional<BankAccount> findById(UUID bankAccountId);

    List<BankAccount> findByUser(User user);

    void makeTransfer(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal transferAmount);

    void deposit(BankAccount account, BigDecimal depositAmount);

    void withdraw(BankAccount account, BigDecimal withdrawalAmount);
}
