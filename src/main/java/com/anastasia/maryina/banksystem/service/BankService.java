package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.model.Bank;

import java.util.List;
import java.util.UUID;

public interface BankService {

    Bank createNewBank();

    List<Bank> findBanksByUserId(UUID userId);

    List<Bank> findAll();
}
