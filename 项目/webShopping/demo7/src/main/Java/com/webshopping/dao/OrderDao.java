package com.webshopping.dao;

import com.webshopping.db.DbSchemaManager;
import com.webshopping.db.DbUtil;
import com.webshopping.model.OrderInfo;
import com.webshopping.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderDao {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public String createOrderFromCart(String orderOwner) throws SQLException {
        DbSchemaManager.ensureSchema();
        if (isBlank(orderOwner)) {
            throw new IllegalArgumentException("orderOwner must not be empty");
        }

        String listCartSql = "SELECT c.[productId], p.[productName], p.[price], c.[quantity] " +
                "FROM [cart] c INNER JOIN [pruduct] p ON c.[productId]=p.[id] " +
                "WHERE c.[cartOwner]=? ORDER BY c.[id]";
        String insertOrderSql = "INSERT INTO [orders]([orderNo], [orderOwner], [totalAmount], [status]) VALUES(?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO [order_item]([orderId], [productId], [productName], [unitPrice], [quantity], [subtotal]) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        String clearCartSql = "DELETE FROM [cart] WHERE [cartOwner]=?";

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(listCartSql)) {
                    ps.setString(1, orderOwner.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            OrderItem item = new OrderItem();
                            item.setProductId(rs.getInt("productId"));
                            item.setProductName(rs.getString("productName"));
                            item.setUnitPrice(rs.getDouble("price"));
                            item.setQuantity(rs.getInt("quantity"));
                            item.setSubtotal(item.getUnitPrice() * item.getQuantity());
                            items.add(item);
                        }
                    }
                }

                if (items.isEmpty()) {
                    conn.rollback();
                    return null;
                }

                double totalAmount = 0D;
                for (OrderItem item : items) {
                    totalAmount += item.getSubtotal();
                }

                String orderNo = generateOrderNo();
                int orderId;
                try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, orderNo);
                    ps.setString(2, orderOwner.trim());
                    ps.setDouble(3, totalAmount);
                    ps.setString(4, STATUS_PENDING);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Failed to create order: no generated id");
                        }
                        orderId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                    for (OrderItem item : items) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setString(3, item.getProductName());
                        ps.setDouble(4, item.getUnitPrice());
                        ps.setInt(5, item.getQuantity());
                        ps.setDouble(6, item.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                try (PreparedStatement ps = conn.prepareStatement(clearCartSql)) {
                    ps.setString(1, orderOwner.trim());
                    ps.executeUpdate();
                }

                conn.commit();
                return orderNo;
            } catch (Exception ex) {
                conn.rollback();
                if (ex instanceof SQLException) {
                    throw (SQLException) ex;
                }
                throw new SQLException("Failed to checkout cart", ex);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public int countOrdersByOwnerAndStatus(String orderOwner, String status) throws SQLException {
        DbSchemaManager.ensureSchema();
        String normalizedStatus = normalizeStatus(status);
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) AS total FROM [orders] WHERE [orderOwner]=?");
        boolean withStatus = !isBlank(normalizedStatus);
        if (withStatus) {
            sql.append(" AND [status]=?");
        }
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, orderOwner);
            if (withStatus) {
                ps.setString(2, normalizedStatus);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public List<OrderInfo> listOrdersByOwnerAndStatus(String orderOwner, String status, int pageNo, int pageSize) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePageNo - 1) * safePageSize;
        String normalizedStatus = normalizeStatus(status);
        boolean withStatus = !isBlank(normalizedStatus);

        StringBuilder sql = new StringBuilder(
                "SELECT [id], [orderNo], [orderOwner], [totalAmount], [status], [createdAt], [updatedAt], [paidAt], [cancelledAt] " +
                "FROM [orders] WHERE [orderOwner]=?");
        if (withStatus) {
            sql.append(" AND [status]=?");
        }
        sql.append(" ORDER BY [id] DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        List<OrderInfo> orders = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, orderOwner);
            if (withStatus) {
                ps.setString(idx++, normalizedStatus);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, safePageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderInfo info = new OrderInfo();
                    info.setId(rs.getInt("id"));
                    info.setOrderNo(rs.getString("orderNo"));
                    info.setOrderOwner(rs.getString("orderOwner"));
                    info.setTotalAmount(rs.getDouble("totalAmount"));
                    info.setStatus(rs.getString("status"));
                    info.setCreatedAt(rs.getTimestamp("createdAt"));
                    info.setUpdatedAt(rs.getTimestamp("updatedAt"));
                    info.setPaidAt(rs.getTimestamp("paidAt"));
                    info.setCancelledAt(rs.getTimestamp("cancelledAt"));
                    orders.add(info);
                }
            }
        }
        return orders;
    }

    public List<OrderItem> listOrderItems(int orderId) throws SQLException {
        Map<Integer, List<OrderItem>> grouped = listOrderItemsByOrderIds(Collections.singletonList(orderId));
        List<OrderItem> items = grouped.get(orderId);
        return items == null ? Collections.<OrderItem>emptyList() : items;
    }

    public Map<Integer, List<OrderItem>> listOrderItemsByOrderIds(List<Integer> orderIds) throws SQLException {
        DbSchemaManager.ensureSchema();
        Map<Integer, List<OrderItem>> grouped = new HashMap<>();
        if (orderIds == null || orderIds.isEmpty()) {
            return grouped;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < orderIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = "SELECT [orderId], [productId], [productName], [unitPrice], [quantity], [subtotal] " +
                "FROM [order_item] WHERE [orderId] IN (" + placeholders + ") ORDER BY [orderId], [id]";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < orderIds.size(); i++) {
                ps.setInt(i + 1, orderIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    List<OrderItem> items = grouped.get(orderId);
                    if (items == null) {
                        items = new ArrayList<>();
                        grouped.put(orderId, items);
                    }
                    OrderItem item = new OrderItem();
                    item.setProductId(rs.getInt("productId"));
                    item.setProductName(rs.getString("productName"));
                    item.setUnitPrice(rs.getDouble("unitPrice"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    items.add(item);
                }
            }
        }
        return grouped;
    }

    public boolean markPaid(String orderOwner, String orderNo) throws SQLException {
        return updateStatus(orderOwner, orderNo, STATUS_PENDING, STATUS_PAID);
    }

    public boolean cancelOrder(String orderOwner, String orderNo) throws SQLException {
        return updateStatus(orderOwner, orderNo, STATUS_PENDING, STATUS_CANCELLED);
    }

    public String getOrderStatusByOwnerAndOrderNo(String orderOwner, String orderNo) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [status] FROM [orders] WHERE [orderOwner]=? AND [orderNo]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderOwner);
            ps.setString(2, orderNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    private boolean updateStatus(String orderOwner, String orderNo, String expectedStatus, String targetStatus) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql;
        if (STATUS_PAID.equals(targetStatus)) {
            sql = "UPDATE [orders] " +
                    "SET [status]=?, [updatedAt]=SYSDATETIME(), [paidAt]=ISNULL([paidAt], SYSDATETIME()) " +
                    "WHERE [orderOwner]=? AND [orderNo]=? AND [status]=?";
        } else if (STATUS_CANCELLED.equals(targetStatus)) {
            sql = "UPDATE [orders] " +
                    "SET [status]=?, [updatedAt]=SYSDATETIME(), [cancelledAt]=ISNULL([cancelledAt], SYSDATETIME()) " +
                    "WHERE [orderOwner]=? AND [orderNo]=? AND [status]=?";
        } else {
            throw new IllegalArgumentException("Unsupported target status: " + targetStatus);
        }
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, targetStatus);
            ps.setString(2, orderOwner);
            ps.setString(3, orderNo);
            ps.setString(4, expectedStatus);
            return ps.executeUpdate() > 0;
        }
    }

    private String generateOrderNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "OD" + System.currentTimeMillis() + suffix;
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "" : status.trim().toUpperCase();
        if (STATUS_PENDING.equals(value) || STATUS_PAID.equals(value) || STATUS_CANCELLED.equals(value)) {
            return value;
        }
        return "";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
