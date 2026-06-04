package com.webshopping.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbUtil {
    static {
        try {
            Class.forName(DbConfig.driverClassName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("SQL Server JDBC driver not found: " + e.getMessage());
        }
    }

    private DbUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String password = DbConfig.password();
        if (password == null || password.trim().isEmpty()) {
            throw new SQLException("Database password is not configured. Set JSP_DB_PASSWORD or -Djsp.db.password.");
        }
        return DriverManager.getConnection(
                DbConfig.url(),
                DbConfig.username(),
                password
        );
    }
}
