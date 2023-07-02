package com.anastasia.maryina.banksystem.dao;

import com.anastasia.maryina.banksystem.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO implements Dao<UUID, User> {
    private final Connection connection;
    private static final Logger log = Logger.getLogger(UserDAO.class.getName());

    public UserDAO() {
        this.connection = ConnectionFactory.getConnection();
    }

    @Override
    public Optional<User> findById(UUID id) {
        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findById()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? and password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, username);
            statement.setObject(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findByUsername()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing findAll()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return userList;
    }

    @Override
    public User save(User entity) {
        String query = "INSERT INTO users (id, name, pid, username, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getName());
            statement.setString(3, entity.getPid());
            statement.setString(4, entity.getUsername());
            statement.setString(5, entity.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing save()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }

        return entity;
    }

    @Override
    public void update(User entity) {
        String query = "UPDATE users SET name = ?, pid = ?, username = ?, password = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getPid());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setObject(5, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing update()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String query = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error occurred while executing delete()", e);
            throw new RuntimeException("An error occurred while executing a database operation.", e);
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error checking if username exists: " + username, e);
            throw new RuntimeException("Error checking if username exists: " + username, e);
        }
        return false;
    }

    public boolean existsByPid(String pid) {
        String sql = "SELECT COUNT(*) FROM users WHERE pid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error checking if pid exists: " + pid, e);
            throw new RuntimeException("Error checking if pid exists: " + pid, e);
        }
        return false;
    }

    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");
        String pid = resultSet.getString("pid");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPid(pid);
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }

}
