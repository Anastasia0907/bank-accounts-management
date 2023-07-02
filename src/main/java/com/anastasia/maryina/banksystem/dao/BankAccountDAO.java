package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.Currency;
import com.anastasia.maryina.banksystem.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankAccountDAO implements Dao<UUID, BankAccount> {
    private final Connection connection;
    private final BankDAO bankDAO;
    private final UserDAO userDAO;
    private static final Logger log = Logger.getLogger(BankAccountDAO.class.getName());

    public BankAccountDAO() {
        this.connection = ConnectionFactory.getConnection();
        this.bankDAO = new BankDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        String query = "SELECT * FROM bank_accounts ba " +
                "JOIN bank_clients bc ON ba.owner_id = bc.id " +
                "JOIN users u ON bc.user_id = u.id " +
                "JOIN banks b ON bc.bank_id = b.id " +
                "WHERE ba.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractBankAccountFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<BankAccount> findAll() {
        String query = "SELECT * FROM bank_accounts ba " +
                "JOIN bank_clients bc ON ba.owner_id = bc.id " +
                "JOIN users u ON bc.user_id = u.id " +
                "JOIN banks b ON bc.bank_id = b.id";
        List<BankAccount> bankAccountList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                bankAccountList.add(extractBankAccountFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return bankAccountList;
    }

    @Override
    public BankAccount save(BankAccount entity) {
        String query = "INSERT INTO bank_accounts (id, owner_id, currency, total_amount) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            statement.setObject(2, entity.getOwner().getId());
            statement.setString(3, entity.getCurrency().name());
            statement.setBigDecimal(4, entity.getTotalAmount());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(BankAccount entity) {
        String query = "UPDATE bank_accounts SET owner_id = ?, currency = ?, total_amount = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getOwner().getId());
            statement.setString(2, entity.getCurrency().name());
            statement.setBigDecimal(3, entity.getTotalAmount());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing update()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM bank_accounts WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    private BankAccount extractBankAccountFromResultSet(ResultSet resultSet) throws SQLException {
        UUID accountId = (UUID) resultSet.getObject("id");
        UUID ownerId = (UUID) resultSet.getObject("owner_id");
        Currency currency = Currency.valueOf(resultSet.getString("currency"));
        BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");

        User user = userDAO.findById((UUID) resultSet.getObject("user_id")).orElse(null);
        Bank bank = bankDAO.findById((UUID) resultSet.getObject("bank_id")).orElse(null);

        BankClient bankClient = new BankClient();
        bankClient.setId(ownerId);
        bankClient.setUser(user);
        bankClient.setClientType(ClientType.valueOf(resultSet.getString("client_type")));
        bankClient.setBank(bank);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        bankAccount.setOwner(bankClient);
        bankAccount.setCurrency(currency);
        bankAccount.setTotalAmount(totalAmount);

        return bankAccount;
    }

    public List<BankAccount> findByUserAndBank(User user, Bank bank) {
        try {
            String query = "SELECT * FROM bank_accounts ba " +
                    "JOIN bank_clients bc ON ba.owner_id = bc.id " +
                    "WHERE bc.user_id = ? AND bc.bank_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, user.getId());
            preparedStatement.setObject(2, bank.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<BankAccount> bankAccounts = new ArrayList<>();

            while (resultSet.next()) {
                bankAccounts.add(extractBankAccountFromResultSet(resultSet));
            }

            return bankAccounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<BankAccount> findByUser(User user) {
        try {
            String query = "SELECT * FROM bank_accounts ba " +
                    "JOIN bank_clients bc ON ba.owner_id = bc.id " +
                    "WHERE bc.user_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<BankAccount> bankAccounts = new ArrayList<>();

            while (resultSet.next()) {
                bankAccounts.add(extractBankAccountFromResultSet(resultSet));
            }

            return bankAccounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
