package com.webshopping.dao;

import com.webshopping.db.DbSchemaManager;
import com.webshopping.db.DbUtil;
import com.webshopping.model.Userinfo;
import com.webshopping.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public Userinfo login(String username, String password) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [userName], [password], [sex], [interest], [role] " +
                "FROM [userinfo] WHERE [userName]=?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String storedPassword = rs.getString("password");
                if (!PasswordUtil.verify(password, storedPassword)) {
                    return null;
                }
                if (PasswordUtil.shouldUpgradeHash(storedPassword)) {
                    updatePasswordHash(conn, username, PasswordUtil.hash(password));
                }
                Userinfo user = new Userinfo();
                user.setUsername(rs.getString("userName"));
                user.setPwd(storedPassword);
                user.setSex(nullToEmpty(rs.getString("sex")));
                user.setHobby(nullToEmpty(rs.getString("interest")));
                user.setRole(nullToEmpty(rs.getString("role")));
                return user;
            }
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT 1 FROM [userinfo] WHERE [userName]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean register(Userinfo user) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "INSERT INTO [userinfo]([userName], [password], [sex], [interest], [role]) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hash(user.getPwd()));
            ps.setString(3, nullToEmpty(user.getSex()));
            ps.setString(4, nullToEmpty(user.getHobby()));
            ps.setString(5, "USER");
            return ps.executeUpdate() > 0;
        }
    }

    public List<Userinfo> listUsers() throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [userName], [sex], [interest], [role] FROM [userinfo] ORDER BY [userName]";
        List<Userinfo> users = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Userinfo user = new Userinfo();
                user.setUsername(rs.getString("userName"));
                user.setSex(nullToEmpty(rs.getString("sex")));
                user.setHobby(nullToEmpty(rs.getString("interest")));
                user.setRole(nullToEmpty(rs.getString("role")));
                users.add(user);
            }
        }
        return users;
    }

    public boolean updateUserRole(String username, String role) throws SQLException {
        DbSchemaManager.ensureSchema();
        String normalizedRole = "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "USER";
        String sql = "UPDATE [userinfo] SET [role]=? WHERE [userName]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedRole);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

    private void updatePasswordHash(Connection conn, String username, String newHash) throws SQLException {
        String updateSql = "UPDATE [userinfo] SET [password]=? WHERE [userName]=?";
        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
            updatePs.setString(1, newHash);
            updatePs.setString(2, username);
            updatePs.executeUpdate();
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
