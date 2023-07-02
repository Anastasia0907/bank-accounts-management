package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.Bank;
import com.anastasia.maryina.banksystem.model.BankClient;
import com.anastasia.maryina.banksystem.model.ClientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BankClientDAO implements Dao<UUID, BankClient> {
    private final Connection connection;
    private final BankDAO bankDAO;

    private static final Logger log = Logger.getLogger(BankClientDAO.class.getName());

    public BankClientDAO() {
        this.connection = ConnectionFactory.getConnection();
        this.bankDAO = new BankDAO();
    }

    @Override
    public Optional<BankClient> findById(UUID id) {
        String query = "SELECT * FROM bank_clients bc " +
                "JOIN users u ON bc.user_id = u.id " +
                "JOIN banks b ON bc.bank_id = b.id " +
                "WHERE bc.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractBankClientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<BankClient> findAll() {
        String query = "SELECT * FROM bank_clients bc " +
                "JOIN users u ON bc.user_id = u.id " +
                "JOIN banks b ON bc.bank_id = b.id";
        List<BankClient> bankClientList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                bankClientList.add(extractBankClientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return bankClientList;
    }

    @Override
    public BankClient save(BankClient entity) {
        String query = "INSERT INTO bank_clients (id, user_id, bank_id, client_type) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            statement.setObject(2, entity.getUser().getId());
            statement.setObject(3, entity.getBank().getId());
            statement.setString(4, entity.getClientType().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(BankClient entity) {
        String query = "UPDATE bank_clients SET user_id = ?, bank_id = ?, client_type = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getUser().getId());
            statement.setObject(2, entity.getBank().getId());
            statement.setString(3, entity.getClientType().name());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing update()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM bank_clients WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    private BankClient extractBankClientFromResultSet(ResultSet resultSet) throws SQLException {
        UUID clientId = (UUID) resultSet.getObject("id");
        UUID bankId = (UUID) resultSet.getObject("bank_id");
        ClientType clientType = ClientType.valueOf(resultSet.getString("client_type"));

        Bank bank = bankDAO.findById(bankId).orElse(null);

        BankClient bankClient = new BankClient();
        bankClient.setId(clientId);
        bankClient.setClientType(clientType);
        bankClient.setBank(bank);

        return bankClient;
    }

    public Optional<BankClient> findByUserBankAndClientType(UUID userId, UUID bankId, ClientType clientType) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM bank_clients WHERE user_id = ? AND bank_id = ? AND client_type = ?")) {
            statement.setObject(1, userId);
            statement.setObject(2, bankId);
            statement.setString(3, clientType.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BankClient bankClient = extractBankClientFromResultSet(resultSet);
                    return Optional.of(bankClient);
                }
            }
        } catch (SQLException e) {
            log.severe("Error occurred while finding BankClient by user, bank, and client type: " + e.getMessage());
            throw new RuntimeException("Failed to find BankClient by user, bank, and client type.", e);
        }
        return Optional.empty();
    }
}
