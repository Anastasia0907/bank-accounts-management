package com.anastasia.maryina.banksystem.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ExchangeRate {

    private UUID bankId;
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rate;

    public ExchangeRate() {
    }

    public ExchangeRate(UUID bankId, Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
        this.bankId = bankId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    public UUID getBankId() {
        return bankId;
    }

    public void setBankId(UUID bankId) {
        this.bankId = bankId;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return bankId.equals(that.bankId) && fromCurrency == that.fromCurrency && toCurrency == that.toCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankId, fromCurrency, toCurrency);
    }
}
