package com.anastasia.maryina.banksystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final Properties properties = new Properties();
    private static final String url = "jdbc:postgresql://localhost/banking_system";
    private static final String testUrl = "jdbc:postgresql://localhost/banking_system_test";

    static {
        properties.put("user", "postgres");
        properties.put("password", "postgres");
        properties.put("useSSL", "false");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection(testUrl, properties);
    }
}
