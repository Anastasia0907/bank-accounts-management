package com.anastasia.maryina.banksystem.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BankClient {

    private UUID id;
    private User user;
    private ClientType clientType;
    private Bank bank;
    private List<BankAccount> accounts;

    public BankClient(User user, ClientType clientType, Bank bank) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.clientType = clientType;
        this.bank = bank;
    }

    public BankClient() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankClient that = (BankClient) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
