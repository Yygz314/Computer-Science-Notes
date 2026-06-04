package com.webshopping.dao;

import com.webshopping.db.DbSchemaManager;
import com.webshopping.db.DbUtil;
import com.webshopping.model.MessageInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_REPLIED = "REPLIED";
    public static final String STATUS_HIDDEN = "HIDDEN";

    public boolean addMessage(String author, String contact, String content) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "INSERT INTO [message_board]([author], [contact], [content], [status]) VALUES(?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, safe(author, 50));
            ps.setString(2, safe(contact, 100));
            ps.setString(3, safe(content, 1000));
            ps.setString(4, STATUS_OPEN);
            return ps.executeUpdate() > 0;
        }
    }

    public List<MessageInfo> listPublicMessages(int limit) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safeLimit = Math.max(1, Math.min(limit, 200));
        String sql = "SELECT TOP (" + safeLimit + ") [id], [author], [contact], [content], [reply], [status], [createdAt], [updatedAt] " +
                "FROM [message_board] WHERE [status] IN (?, ?) ORDER BY [id] DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, STATUS_OPEN);
            ps.setString(2, STATUS_REPLIED);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        }
    }

    public List<MessageInfo> listAllMessages(int limit) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safeLimit = Math.max(1, Math.min(limit, 500));
        String sql = "SELECT TOP (" + safeLimit + ") [id], [author], [contact], [content], [reply], [status], [createdAt], [updatedAt] " +
                "FROM [message_board] ORDER BY [id] DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        }
    }

    public boolean updateMessageReply(int id, String reply, String status) throws SQLException {
        DbSchemaManager.ensureSchema();
        String normalizedStatus = normalizeStatus(status);
        String sql = "UPDATE [message_board] SET [reply]=?, [status]=?, [updatedAt]=SYSDATETIME() WHERE [id]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, safe(reply, 1000));
            ps.setString(2, normalizedStatus);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<MessageInfo> mapList(ResultSet rs) throws SQLException {
        List<MessageInfo> list = new ArrayList<>();
        while (rs.next()) {
            MessageInfo m = new MessageInfo();
            m.setId(rs.getInt("id"));
            m.setAuthor(rs.getString("author"));
            m.setContact(rs.getString("contact"));
            m.setContent(rs.getString("content"));
            m.setReply(rs.getString("reply"));
            m.setStatus(rs.getString("status"));
            m.setCreatedAt(rs.getTimestamp("createdAt"));
            m.setUpdatedAt(rs.getTimestamp("updatedAt"));
            list.add(m);
        }
        return list;
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "" : status.trim().toUpperCase();
        if (STATUS_OPEN.equals(value) || STATUS_REPLIED.equals(value) || STATUS_HIDDEN.equals(value)) {
            return value;
        }
        return STATUS_OPEN;
    }

    private String safe(String value, int maxLen) {
        String v = value == null ? "" : value.trim();
        if (v.length() > maxLen) {
            return v.substring(0, maxLen);
        }
        return v;
    }
}
