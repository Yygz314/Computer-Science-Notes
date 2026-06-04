package com.webshopping.dao;

import com.webshopping.db.DbSchemaManager;
import com.webshopping.db.DbUtil;
import com.webshopping.model.CartItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    public void addItem(String cartOwner, int productId, int quantity) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safeQty = quantity < 1 ? 1 : Math.min(quantity, 99);

        String querySql = "SELECT [id], [quantity] FROM [cart] WHERE [cartOwner]=? AND [productId]=?";
        String insertSql = "INSERT INTO [cart]([cartOwner], [productId], [quantity]) VALUES(?, ?, ?)";
        String updateSql = "UPDATE [cart] SET [quantity]=?, [updatedAt]=SYSDATETIME() WHERE [id]=?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement queryPs = conn.prepareStatement(querySql)) {
            queryPs.setString(1, cartOwner);
            queryPs.setInt(2, productId);

            try (ResultSet rs = queryPs.executeQuery()) {
                if (rs.next()) {
                    int rowId = rs.getInt("id");
                    int oldQty = rs.getInt("quantity");
                    int newQty = Math.min(oldQty + safeQty, 99);
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setInt(1, newQty);
                        updatePs.setInt(2, rowId);
                        updatePs.executeUpdate();
                    }
                    return;
                }
            }

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, cartOwner);
                insertPs.setInt(2, productId);
                insertPs.setInt(3, safeQty);
                insertPs.executeUpdate();
            }
        }
    }

    public List<CartItem> listItems(String cartOwner) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT c.[productId], p.[productName], p.[price], c.[quantity] " +
                "FROM [cart] c " +
                "INNER JOIN [pruduct] p ON c.[productId] = p.[id] " +
                "WHERE c.[cartOwner]=? ORDER BY c.[id]";
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartOwner);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("productId"));
                    item.setName(rs.getString("productName"));
                    item.setPrice(rs.getDouble("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public double getTotalPrice(String cartOwner) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT ISNULL(SUM(p.[price] * c.[quantity]), 0) AS totalPrice " +
                "FROM [cart] c " +
                "INNER JOIN [pruduct] p ON c.[productId] = p.[id] " +
                "WHERE c.[cartOwner]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartOwner);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totalPrice");
                }
            }
        }
        return 0;
    }

    public void clear(String cartOwner) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "DELETE FROM [cart] WHERE [cartOwner]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartOwner);
            ps.executeUpdate();
        }
    }

    public void removeItem(String cartOwner, int productId) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "DELETE FROM [cart] WHERE [cartOwner]=? AND [productId]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartOwner);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public void updateQuantity(String cartOwner, int productId, int quantity) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safeQty = quantity < 1 ? 1 : Math.min(quantity, 99);
        String sql = "UPDATE [cart] SET [quantity]=?, [updatedAt]=SYSDATETIME() WHERE [cartOwner]=? AND [productId]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, safeQty);
            ps.setString(2, cartOwner);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }
    }
}
