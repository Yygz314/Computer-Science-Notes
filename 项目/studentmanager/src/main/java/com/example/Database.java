package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:students.db";

    private static volatile Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (Database.class) {
                if (connection == null) {
                    try {
                        connection = DriverManager.getConnection(URL);
                        initSchema(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
        return connection;
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 基础表
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "score INTEGER," +
                    "gender TEXT DEFAULT ''," +
                    "clazz  TEXT DEFAULT ''" +
                    ");");
        }

        // 兼容旧数据库：尝试添加新列，已存在则忽略
        for (String col : new String[]{"gender TEXT DEFAULT ''", "clazz TEXT DEFAULT ''"}) {
            try (Statement s = conn.createStatement()) {
                s.execute("ALTER TABLE students ADD COLUMN " + col);
            } catch (SQLException ignored) {
                // 列已存在，正常跳过
            }
        }
    }
}
