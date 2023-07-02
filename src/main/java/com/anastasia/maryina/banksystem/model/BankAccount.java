package com.anastasia.maryina.banksystem.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BankAccount {

    private UUID id;
    private BankClient owner;
    private Currency currency;
    private BigDecimal totalAmount;
    private List<Transaction> transactions;

    public BankAccount() {
    }

    public BankAccount(BankClient owner, Currency currency, BigDecimal initialBalance) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.currency = currency;
        this.totalAmount = initialBalance;
    }

    public BankAccount(UUID accountId, Currency currency, BigDecimal totalAmount, BankClient owner) {
        this.id = accountId;
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BankClient getOwner() {
        return owner;
    }

    public void setOwner(BankClient owner) {
        this.owner = owner;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getTransferCommission() {
        return this.owner.getClientType() == ClientType.INDIVIDUAL
                ? this.owner.getBank().getIndividualsTransferCommission()
                : this.owner.getBank().getLegalEntitiesTransferCommission();
    }

    public void debit(BigDecimal transferAmount) {
        this.totalAmount = this.totalAmount.add(transferAmount);
    }

    public void credit(BigDecimal transferAmount) {
        this.totalAmount = this.totalAmount.subtract(transferAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
