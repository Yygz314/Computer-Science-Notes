package com.webshopping.dao;

import com.webshopping.db.DbSchemaManager;
import com.webshopping.db.DbUtil;
import com.webshopping.model.ProductInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    public int countAll() throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT COUNT(1) AS total FROM [pruduct]";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public List<ProductInfo> findAll() throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] ORDER BY [id]";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapProducts(rs);
        }
    }

    public List<ProductInfo> findPage(int pageNo, int pageSize) throws SQLException {
        DbSchemaManager.ensureSchema();
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePageNo - 1) * safePageSize;

        String sql = "SELECT [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] ORDER BY [id] OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, safePageSize);
            try (ResultSet rs = ps.executeQuery()) {
                return mapProducts(rs);
            }
        }
    }

    public int countByKeyword(String keyword) throws SQLException {
        String normalized = normalizeKeyword(keyword);
        if (normalized.isEmpty()) {
            return countAll();
        }
        DbSchemaManager.ensureSchema();
        String sql = "SELECT COUNT(1) AS total FROM [pruduct] WHERE [productName] LIKE ? ESCAPE '\\'";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toLikePattern(normalized));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public List<ProductInfo> findPageByKeyword(String keyword, int pageNo, int pageSize) throws SQLException {
        String normalized = normalizeKeyword(keyword);
        if (normalized.isEmpty()) {
            return findPage(pageNo, pageSize);
        }
        DbSchemaManager.ensureSchema();
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePageNo - 1) * safePageSize;
        String sql = "SELECT [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] WHERE [productName] LIKE ? ESCAPE '\\' " +
                "ORDER BY [id] OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toLikePattern(normalized));
            ps.setInt(2, offset);
            ps.setInt(3, safePageSize);
            try (ResultSet rs = ps.executeQuery()) {
                return mapProducts(rs);
            }
        }
    }

    public List<ProductInfo> findLatest(int limit) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT TOP (" + Math.max(limit, 1) + ") [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] ORDER BY [id] DESC";
        List<ProductInfo> latest = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            latest.addAll(mapProducts(rs));
        }
        latest.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        return latest;
    }

    public List<ProductInfo> findHot(int limit) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT TOP (" + Math.max(limit, 1) + ") [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] ORDER BY [soldCount] DESC, [id] ASC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapProducts(rs);
        }
    }

    public ProductInfo findById(int id) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] WHERE [id]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapProduct(rs);
            }
        }
    }

    public List<ProductInfo> findByCategoryId(int categoryId) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "SELECT [id], [productName], [price], [originalPrice], [soldCount], [clickCount], [categoryId], [imagePath], [detailImagePath] " +
                "FROM [pruduct] WHERE [categoryId]=? ORDER BY [id]";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapProducts(rs);
            }
        }
    }

    private List<ProductInfo> mapProducts(ResultSet rs) throws SQLException {
        List<ProductInfo> products = new ArrayList<>();
        while (rs.next()) {
            products.add(mapProduct(rs));
        }
        return products;
    }

    private ProductInfo mapProduct(ResultSet rs) throws SQLException {
        return new ProductInfo(
                rs.getInt("id"),
                rs.getString("productName"),
                rs.getDouble("price"),
                rs.getDouble("originalPrice"),
                rs.getInt("soldCount"),
                rs.getInt("clickCount"),
                rs.getInt("categoryId"),
                rs.getString("imagePath"),
                rs.getString("detailImagePath")
        );
    }

    public void increaseClickCount(int productId) throws SQLException {
        DbSchemaManager.ensureSchema();
        String sql = "UPDATE [pruduct] SET [clickCount]=ISNULL([clickCount],0)+1 WHERE [id]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public boolean updateProduct(ProductInfo product) throws SQLException {
        if (product == null || product.getId() <= 0) {
            return false;
        }
        DbSchemaManager.ensureSchema();
        String sql = "UPDATE [pruduct] SET [productName]=?, [price]=?, [originalPrice]=?, [soldCount]=?, [categoryId]=?, [imagePath]=?, [detailImagePath]=? WHERE [id]=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setDouble(3, product.getOriginalPrice());
            ps.setInt(4, product.getSoldCount());
            ps.setInt(5, product.getCategoryId());
            ps.setString(6, product.getImagePath());
            ps.setString(7, product.getDetailImagePath());
            ps.setInt(8, product.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private String toLikePattern(String keyword) {
        String escaped = keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_")
                .replace("[", "\\[");
        return "%" + escaped + "%";
    }
}
