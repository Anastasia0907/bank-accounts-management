package com.anastasia.maryina.banksystem.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Bank {

    private UUID id;
    private String name;
    private BigDecimal individualsTransferCommission;
    private BigDecimal legalEntitiesTransferCommission;
    private List<BankClient> clients;
    private List<ExchangeRate> exchangeRates;

    public Bank() {
    }

    public Bank(String name, BigDecimal individualsTransferCommission, BigDecimal legalEntitiesTransferCommission) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.individualsTransferCommission = individualsTransferCommission;
        this.legalEntitiesTransferCommission = legalEntitiesTransferCommission;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIndividualsTransferCommission() {
        return individualsTransferCommission;
    }

    public void setIndividualsTransferCommission(BigDecimal individualsTransferCommission) {
        this.individualsTransferCommission = individualsTransferCommission;
    }

    public BigDecimal getLegalEntitiesTransferCommission() {
        return legalEntitiesTransferCommission;
    }

    public void setLegalEntitiesTransferCommission(BigDecimal legalEntitiesTransferCommission) {
        this.legalEntitiesTransferCommission = legalEntitiesTransferCommission;
    }

    public List<BankClient> getClients() {
        return clients;
    }

    public void setClients(List<BankClient> clients) {
        this.clients = clients;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return id.equals(bank.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
