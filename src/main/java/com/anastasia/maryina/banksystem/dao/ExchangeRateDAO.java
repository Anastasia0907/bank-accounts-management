package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.Bank;
import com.anastasia.maryina.banksystem.model.Currency;
import com.anastasia.maryina.banksystem.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExchangeRateDAO implements Dao<UUID, ExchangeRate> {
    private final Connection connection;
    private static final Logger log = Logger.getLogger(ExchangeRateDAO.class.getName());

    public ExchangeRateDAO() {
        this.connection = ConnectionFactory.getConnection();
    }

    @Override
    public Optional<ExchangeRate> findById(UUID id) {
        String query = "SELECT * FROM exchange_rates WHERE bank_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractExchangeRateFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        String query = "SELECT * FROM exchange_rates";
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                exchangeRateList.add(extractExchangeRateFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return exchangeRateList;
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        String query = "INSERT INTO exchange_rates (bank_id, from_currency, to_currency, rate) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getBankId());
            statement.setString(2, entity.getFromCurrency().name());
            statement.setString(3, entity.getToCurrency().name());
            statement.setBigDecimal(4, entity.getRate());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(ExchangeRate entity) {
        String query = "UPDATE exchange_rates SET from_currency = ?, to_currency = ?, rate = ? " +
                "WHERE bank_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getFromCurrency().name());
            statement.setString(2, entity.getToCurrency().name());
            statement.setBigDecimal(3, entity.getRate());
            statement.setObject(4, entity.getBankId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing update()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM exchange_rates WHERE bank_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    private ExchangeRate extractExchangeRateFromResultSet(ResultSet resultSet) throws SQLException {
        UUID bankId = (UUID) resultSet.getObject("bank_id");
        Currency fromCurrency = Currency.valueOf(resultSet.getString("from_currency"));
        Currency toCurrency = Currency.valueOf(resultSet.getString("to_currency"));
        BigDecimal exchangeRate = resultSet.getBigDecimal("rate");

        ExchangeRate rate = new ExchangeRate();
        rate.setBankId(bankId);
        rate.setFromCurrency(fromCurrency);
        rate.setToCurrency(toCurrency);
        rate.setRate(exchangeRate);

        return rate;
    }

    public void saveAll(ArrayList<ExchangeRate> exchangeRates) {
        String sql = "INSERT INTO exchange_rates (bank_id, from_currency, to_currency, rate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ExchangeRate entity : exchangeRates) {
                statement.setObject(1, entity.getBankId());
                statement.setString(2, entity.getFromCurrency().toString());
                statement.setString(3, entity.getToCurrency().toString());
                statement.setBigDecimal(4, entity.getRate());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error saving exchange rates", e);
            throw new RuntimeException("Error saving exchange rates", e);
        }
    }

    public ExchangeRate getExchangeRate(Bank sourceBank, Currency sourceCurrency, Currency destinationCurrency) {
        String query = "SELECT * FROM exchange_rates WHERE bank_id = ? AND from_currency = ? AND to_currency = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setObject(1, sourceBank.getId());
            statement.setString(2, sourceCurrency.name());
            statement.setString(3, destinationCurrency.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    return new ExchangeRate(sourceBank.getId(), sourceCurrency, destinationCurrency, rate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
