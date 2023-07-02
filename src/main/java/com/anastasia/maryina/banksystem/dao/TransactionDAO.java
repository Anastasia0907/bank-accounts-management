package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.BankAccount;
import com.anastasia.maryina.banksystem.model.Transaction;
import com.anastasia.maryina.banksystem.model.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionDAO implements Dao<UUID, Transaction> {
    private final Connection connection;
    private static final Logger log = Logger.getLogger(TransactionDAO.class.getName());

    public TransactionDAO() {
        this.connection = ConnectionFactory.getConnection();
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        String query = "SELECT * FROM transactions WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractTransactionFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        String query = "SELECT * FROM transactions";
        List<Transaction> transactionList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                transactionList.add(extractTransactionFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return transactionList;
    }

    @Override
    public Transaction save(Transaction entity) {
        String query = "INSERT INTO transactions (id, transaction_type, account_id, amount, commission, total, date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getTransactionType().name());
            statement.setObject(3, entity.getAccountId());
            statement.setBigDecimal(4, entity.getAmount());
            statement.setBigDecimal(5, entity.getBankCommission());
            statement.setBigDecimal(6, entity.getTotal());
            statement.setObject(7, entity.getDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(Transaction entity) {
        log.info("Update operation is not supported for transactions.");
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    private Transaction extractTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = (UUID) resultSet.getObject("id");
        UUID toAccount = (UUID) resultSet.getObject("account_id");
        BigDecimal amount = resultSet.getBigDecimal("amount");
        BigDecimal commission = resultSet.getBigDecimal("commission");
        BigDecimal total = resultSet.getBigDecimal("total");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transaction_type"));

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAccountId(toAccount);
        transaction.setAmount(amount);
        transaction.setBankCommission(commission);
        transaction.setTotal(total);
        transaction.setDate(date);
        transaction.setTransactionType(transactionType);

        return transaction;
    }

    public List<Transaction> findByAccountAndDateRange(BankAccount account, LocalDate fromDate, LocalDate toDate) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM transactions " +
                             "WHERE account_id = ? " +
                             "AND date >= ? AND date <= ? order by date desc ")) {

            statement.setObject(1, account.getId());
            statement.setObject(2, fromDate);
            statement.setObject(3, toDate);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = extractTransactionFromResultSet(resultSet);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
