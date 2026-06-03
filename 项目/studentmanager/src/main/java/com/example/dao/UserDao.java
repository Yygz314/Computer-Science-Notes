package com.example.dao;

import com.example.Database;
import com.example.bean.UserBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    public static Optional<UserBean> login(String username, String password) {
        try (PreparedStatement stmt = Database.getConnection().prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                UserBean user = new UserBean(username, storedPasswordHash);
                if (user.validatePassword(password)) {
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void update(UserBean user) {
        if (user.isUpdated()) {
            try (PreparedStatement stmt = Database.getConnection().prepareStatement("UPDATE users SET password=? WHERE username=?")) {
                stmt.setString(1, user.getPasswordHash());
                stmt.setString(2, user.getUsername());
                stmt.executeUpdate();
                user.setUpdated(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
