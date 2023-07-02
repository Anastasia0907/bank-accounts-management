package com.anastasia.maryina.banksystem.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private UUID accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal bankCommission;
    private BigDecimal total;
    private LocalDate date;

    public Transaction() {
    }

    public Transaction(TransactionType transactionType, BankAccount account, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.transactionType = transactionType;
        this.accountId = account.getId();
        this.amount = amount;
        this.bankCommission = BigDecimal.ZERO;
        this.total = amount;
        this.date = LocalDate.now();
    }

    public Transaction(TransactionType transactionType, BankAccount account, BigDecimal amount, BigDecimal bankCommission) {
        this.id = UUID.randomUUID();
        this.transactionType = transactionType;
        this.accountId = account.getId();
        this.amount = amount;
        this.bankCommission = bankCommission;
        this.total = amount.add(bankCommission);
        this.date = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBankCommission() {
        return bankCommission;
    }

    public void setBankCommission(BigDecimal bankCommission) {
        this.bankCommission = bankCommission;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionType=" + transactionType +
                ", amount=" + amount +
                ", bankCommission=" + bankCommission +
                ", total=" + total +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
