package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.Bank;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankDAO implements Dao<UUID, Bank> {
    private final Connection connection;
    private static final Logger log = Logger.getLogger(BankDAO.class.getName());

    public BankDAO() {
        this.connection = ConnectionFactory.getConnection();
    }

    @Override
    public Optional<Bank> findById(UUID id) {
        String query = "SELECT * FROM banks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractBankFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Bank> findAll() {
        String query = "SELECT * FROM banks";
        List<Bank> bankList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                bankList.add(extractBankFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return bankList;
    }

    @Override
    public Bank save(Bank entity) {
        String query = "INSERT INTO banks (id, name, individuals_transfer_commission, legal_entities_transfer_commission) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getName());
            statement.setBigDecimal(3, entity.getIndividualsTransferCommission());
            statement.setBigDecimal(4, entity.getLegalEntitiesTransferCommission());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(Bank entity) {
        String query = "UPDATE banks SET name = ?, individuals_transfer_commission = ?, legal_entities_transfer_commission = ? " +
                "WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getName());
            statement.setBigDecimal(2, entity.getIndividualsTransferCommission());
            statement.setBigDecimal(3, entity.getLegalEntitiesTransferCommission());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing update()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM banks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    private Bank extractBankFromResultSet(ResultSet resultSet) throws SQLException {
        UUID bankId = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");
        BigDecimal individualsTransferCommission = resultSet.getBigDecimal("individuals_transfer_commission");
        BigDecimal legalEntitiesTransferCommission = resultSet.getBigDecimal("legal_entities_transfer_commission");

        Bank bank = new Bank();
        bank.setId(bankId);
        bank.setName(name);
        bank.setIndividualsTransferCommission(individualsTransferCommission);
        bank.setLegalEntitiesTransferCommission(legalEntitiesTransferCommission);

        return bank;
    }

    public List<Bank> findBanksByUserId(UUID userId) {
        List<Bank> banks = new ArrayList<>();
        String query = "SELECT b.* FROM banks b " +
                "LEFT JOIN bank_clients bc ON bc.bank_id = b.id " +
                "WHERE bc.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bank bank = extractBankFromResultSet(resultSet);
                    banks.add(bank);
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while fetching user bank list", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return banks;
    }
}
