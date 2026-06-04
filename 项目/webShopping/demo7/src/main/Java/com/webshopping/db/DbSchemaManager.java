package com.webshopping.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbSchemaManager {
    private static volatile boolean initialized = false;

    private DbSchemaManager() {
    }

    public static void ensureSchema() throws SQLException {
        if (initialized) {
            return;
        }
        synchronized (DbSchemaManager.class) {
            if (initialized) {
                return;
            }
            try (Connection conn = DbUtil.getConnection();
                 Statement st = conn.createStatement()) {
                st.execute(buildCreateUserinfoTableSql());
                st.execute(buildCreatePruductTableSql());
                st.execute(buildCreateCartTableSql());
                st.execute(buildCreateOrdersTableSql());
                st.execute(buildCreateOrderItemTableSql());
                st.execute(buildCreateMessageTableSql());
                st.execute(buildMigrateCartSql());
                st.execute(buildEnsurePruductClickCountColumnSql());
                st.execute(buildNormalizePruductClickCountSql());
                st.execute(buildEnsureUserPasswordColumnSql());
                st.execute(buildEnsureUserRoleColumnSql());
                st.execute(buildNormalizeUserRoleSql());
                st.execute(buildEnsureUserRoleConstraintSql());
                st.execute(buildNormalizeMessageStatusSql());
                st.execute(buildEnsureMessageStatusConstraintSql());
                st.execute(buildEnsureMessageStatusIndexSql());
                st.execute(buildNormalizeCartSql());
                st.execute(buildCartConstraintsSql());
                st.execute(buildOrderConstraintsSql());
                st.execute(buildSeedPruductSql());
            }
            initialized = true;
        }
    }

    private static String buildCreatePruductTableSql() {
        return "IF OBJECT_ID(N'dbo.pruduct', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.pruduct (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    productName NVARCHAR(100) NOT NULL, " +
                "    price DECIMAL(10,2) NOT NULL, " +
                "    originalPrice DECIMAL(10,2) NOT NULL, " +
                "    soldCount INT NOT NULL DEFAULT 0, " +
                "    categoryId INT NOT NULL DEFAULT 1, " +
                "    imagePath NVARCHAR(255) NOT NULL, " +
                "    detailImagePath NVARCHAR(255) NOT NULL, " +
                "    createdAt DATETIME2 NOT NULL DEFAULT SYSDATETIME() " +
                "  ) " +
                "END";
    }

    private static String buildCreateUserinfoTableSql() {
        return "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.userinfo (" +
                "    userName NVARCHAR(50) NOT NULL PRIMARY KEY, " +
                "    password NVARCHAR(255) NOT NULL, " +
                "    sex NVARCHAR(20) NULL, " +
                "    interest NVARCHAR(255) NULL, " +
                "    role NVARCHAR(20) NOT NULL DEFAULT N'USER' " +
                "  ) " +
                "END";
    }

    private static String buildCreateCartTableSql() {
        return "IF OBJECT_ID(N'dbo.cart', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.cart (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    cartOwner NVARCHAR(100) NOT NULL, " +
                "    productId INT NOT NULL, " +
                "    quantity INT NOT NULL DEFAULT 1, " +
                "    createdAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                "    updatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME() " +
                "  ) " +
                "END";
    }

    private static String buildCreateOrdersTableSql() {
        return "IF OBJECT_ID(N'dbo.orders', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.orders (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    orderNo NVARCHAR(40) NOT NULL, " +
                "    orderOwner NVARCHAR(100) NOT NULL, " +
                "    totalAmount DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "    status NVARCHAR(20) NOT NULL DEFAULT N'PENDING', " +
                "    createdAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                "    updatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                "    paidAt DATETIME2 NULL, " +
                "    cancelledAt DATETIME2 NULL " +
                "  ) " +
                "END";
    }

    private static String buildCreateOrderItemTableSql() {
        return "IF OBJECT_ID(N'dbo.order_item', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.order_item (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    orderId INT NOT NULL, " +
                "    productId INT NOT NULL, " +
                "    productName NVARCHAR(100) NOT NULL, " +
                "    unitPrice DECIMAL(10,2) NOT NULL, " +
                "    quantity INT NOT NULL, " +
                "    subtotal DECIMAL(10,2) NOT NULL " +
                "  ) " +
                "END";
    }

    private static String buildCreateMessageTableSql() {
        return "IF OBJECT_ID(N'dbo.message_board', N'U') IS NULL " +
                "BEGIN " +
                "  CREATE TABLE dbo.message_board (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    author NVARCHAR(50) NOT NULL, " +
                "    contact NVARCHAR(100) NULL, " +
                "    content NVARCHAR(1000) NOT NULL, " +
                "    reply NVARCHAR(1000) NULL, " +
                "    status NVARCHAR(20) NOT NULL DEFAULT N'OPEN', " +
                "    createdAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                "    updatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME() " +
                "  ) " +
                "END";
    }

    private static String buildMigrateCartSql() {
        return "IF OBJECT_ID(N'dbo.cart', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.cart', 'productName') IS NOT NULL " +
                "BEGIN " +
                "  IF OBJECT_ID(N'dbo.cart_tmp', N'U') IS NOT NULL DROP TABLE dbo.cart_tmp; " +
                "  CREATE TABLE dbo.cart_tmp (" +
                "    id INT IDENTITY(1,1) PRIMARY KEY, " +
                "    cartOwner NVARCHAR(100) NOT NULL, " +
                "    productId INT NOT NULL, " +
                "    quantity INT NOT NULL DEFAULT 1, " +
                "    createdAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(), " +
                "    updatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME() " +
                "  ); " +
                "  INSERT INTO dbo.cart_tmp(cartOwner, productId, quantity, createdAt, updatedAt) " +
                "  SELECT cartOwner, productId, " +
                "         CASE WHEN SUM(CASE WHEN quantity < 1 THEN 1 ELSE quantity END) > 99 " +
                "              THEN 99 ELSE SUM(CASE WHEN quantity < 1 THEN 1 ELSE quantity END) END AS quantity, " +
                "         MIN(ISNULL(createdAt, SYSDATETIME())), MAX(ISNULL(updatedAt, SYSDATETIME())) " +
                "  FROM dbo.cart " +
                "  GROUP BY cartOwner, productId; " +
                "  DROP TABLE dbo.cart; " +
                "  EXEC sp_rename 'dbo.cart_tmp', 'cart'; " +
                "END";
    }

    private static String buildNormalizeCartSql() {
        return "IF OBJECT_ID(N'dbo.cart', N'U') IS NOT NULL " +
                "BEGIN " +
                "  UPDATE dbo.cart SET quantity = 1 WHERE quantity < 1; " +
                "  UPDATE dbo.cart SET quantity = 99 WHERE quantity > 99; " +
                "  DELETE c FROM dbo.cart c LEFT JOIN dbo.pruduct p ON c.productId = p.id WHERE p.id IS NULL; " +
                "END";
    }

    private static String buildEnsurePruductClickCountColumnSql() {
        return "IF OBJECT_ID(N'dbo.pruduct', N'U') IS NOT NULL " +
                "BEGIN " +
                "  IF COL_LENGTH('dbo.pruduct', 'clickCount') IS NULL " +
                "    ALTER TABLE dbo.pruduct ADD clickCount INT NOT NULL CONSTRAINT DF_pruduct_clickCount DEFAULT 0; " +
                "END";
    }

    private static String buildNormalizePruductClickCountSql() {
        return "IF OBJECT_ID(N'dbo.pruduct', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.pruduct', 'clickCount') IS NOT NULL " +
                "BEGIN " +
                "  UPDATE dbo.pruduct SET clickCount = 0 WHERE clickCount IS NULL; " +
                "END";
    }

    private static String buildEnsureUserRoleColumnSql() {
        return "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NOT NULL " +
                "BEGIN " +
                "  IF COL_LENGTH('dbo.userinfo', 'role') IS NULL " +
                "    ALTER TABLE dbo.userinfo ADD role NVARCHAR(20) NOT NULL CONSTRAINT DF_userinfo_role DEFAULT N'USER'; " +
                "END";
    }

    private static String buildEnsureUserPasswordColumnSql() {
        return "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.userinfo', 'password') IS NOT NULL " +
                "AND COL_LENGTH('dbo.userinfo', 'password') < 510 " +
                "BEGIN " +
                "  ALTER TABLE dbo.userinfo ALTER COLUMN password NVARCHAR(255) NOT NULL; " +
                "END";
    }

    private static String buildNormalizeUserRoleSql() {
        return "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.userinfo', 'role') IS NOT NULL " +
                "BEGIN " +
                "  UPDATE dbo.userinfo SET role = N'USER' WHERE role IS NULL OR LTRIM(RTRIM(role)) = N''; " +
                "  UPDATE dbo.userinfo SET role = UPPER(LTRIM(RTRIM(role))); " +
                "END";
    }

    private static String buildEnsureUserRoleConstraintSql() {
        return "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.userinfo', 'role') IS NOT NULL " +
                "AND NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = N'CK_userinfo_role') " +
                "BEGIN " +
                "  ALTER TABLE dbo.userinfo ADD CONSTRAINT CK_userinfo_role CHECK (role IN (N'USER', N'ADMIN')); " +
                "END";
    }

    private static String buildNormalizeMessageStatusSql() {
        return "IF OBJECT_ID(N'dbo.message_board', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.message_board', 'status') IS NOT NULL " +
                "BEGIN " +
                "  UPDATE dbo.message_board SET status = N'OPEN' WHERE status IS NULL OR LTRIM(RTRIM(status)) = N''; " +
                "  UPDATE dbo.message_board SET status = UPPER(LTRIM(RTRIM(status))); " +
                "END";
    }

    private static String buildEnsureMessageStatusConstraintSql() {
        return "IF OBJECT_ID(N'dbo.message_board', N'U') IS NOT NULL " +
                "AND COL_LENGTH('dbo.message_board', 'status') IS NOT NULL " +
                "AND NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = N'CK_message_board_status') " +
                "BEGIN " +
                "  ALTER TABLE dbo.message_board ADD CONSTRAINT CK_message_board_status CHECK (status IN (N'OPEN', N'REPLIED', N'HIDDEN')); " +
                "END";
    }

    private static String buildEnsureMessageStatusIndexSql() {
        return "IF OBJECT_ID(N'dbo.message_board', N'U') IS NOT NULL " +
                "AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_message_board_status_created' AND object_id = OBJECT_ID(N'dbo.message_board')) " +
                "BEGIN " +
                "  CREATE INDEX IX_message_board_status_created ON dbo.message_board(status, createdAt DESC); " +
                "END";
    }

    private static String buildCartConstraintsSql() {
        return "IF OBJECT_ID(N'dbo.cart', N'U') IS NOT NULL " +
                "BEGIN " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_cart_owner' AND object_id = OBJECT_ID(N'dbo.cart')) " +
                "    CREATE INDEX IX_cart_owner ON dbo.cart(cartOwner); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_cart_owner_product' AND object_id = OBJECT_ID(N'dbo.cart')) " +
                "    CREATE UNIQUE INDEX UX_cart_owner_product ON dbo.cart(cartOwner, productId); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_cart_pruduct') " +
                "    ALTER TABLE dbo.cart ADD CONSTRAINT FK_cart_pruduct FOREIGN KEY(productId) REFERENCES dbo.pruduct(id) ON DELETE CASCADE; " +
                "END";
    }

    private static String buildOrderConstraintsSql() {
        return "IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL " +
                "BEGIN " +
                "  IF COL_LENGTH('dbo.orders', 'updatedAt') IS NULL " +
                "    ALTER TABLE dbo.orders ADD updatedAt DATETIME2 NOT NULL CONSTRAINT DF_orders_updatedAt DEFAULT SYSDATETIME(); " +
                "  IF COL_LENGTH('dbo.orders', 'paidAt') IS NULL " +
                "    ALTER TABLE dbo.orders ADD paidAt DATETIME2 NULL; " +
                "  IF COL_LENGTH('dbo.orders', 'cancelledAt') IS NULL " +
                "    ALTER TABLE dbo.orders ADD cancelledAt DATETIME2 NULL; " +
                "  UPDATE dbo.orders SET status = UPPER(LTRIM(RTRIM(status))) WHERE status IS NOT NULL; " +
                "  UPDATE dbo.orders SET status = N'PENDING' WHERE status IS NULL OR LTRIM(RTRIM(status)) = N''; " +
                "  UPDATE dbo.orders SET updatedAt = createdAt WHERE updatedAt IS NULL; " +
                "  UPDATE dbo.orders SET paidAt = ISNULL(paidAt, updatedAt) WHERE status = N'PAID' AND paidAt IS NULL; " +
                "  UPDATE dbo.orders SET cancelledAt = ISNULL(cancelledAt, updatedAt) WHERE status = N'CANCELLED' AND cancelledAt IS NULL; " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = N'CK_orders_status') " +
                "    ALTER TABLE dbo.orders ADD CONSTRAINT CK_orders_status CHECK (status IN (N'PENDING', N'PAID', N'CANCELLED')); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_orders_orderNo' AND object_id = OBJECT_ID(N'dbo.orders')) " +
                "    CREATE UNIQUE INDEX UX_orders_orderNo ON dbo.orders(orderNo); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_orders_owner' AND object_id = OBJECT_ID(N'dbo.orders')) " +
                "    CREATE INDEX IX_orders_owner ON dbo.orders(orderOwner); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_orders_owner_status_created' AND object_id = OBJECT_ID(N'dbo.orders')) " +
                "    CREATE INDEX IX_orders_owner_status_created ON dbo.orders(orderOwner, status, createdAt DESC); " +
                "END; " +
                "IF OBJECT_ID(N'dbo.order_item', N'U') IS NOT NULL " +
                "BEGIN " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_order_item_orderId' AND object_id = OBJECT_ID(N'dbo.order_item')) " +
                "    CREATE INDEX IX_order_item_orderId ON dbo.order_item(orderId); " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_order_item_orders') " +
                "    ALTER TABLE dbo.order_item ADD CONSTRAINT FK_order_item_orders FOREIGN KEY(orderId) REFERENCES dbo.orders(id) ON DELETE CASCADE; " +
                "  IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_order_item_pruduct') " +
                "    ALTER TABLE dbo.order_item ADD CONSTRAINT FK_order_item_pruduct FOREIGN KEY(productId) REFERENCES dbo.pruduct(id); " +
                "END";
    }

    private static String buildSeedPruductSql() {
        return ""
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/1.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Dual-color Bamboo Fan Pink', 18, 28, 86, 1, N'Picture/1.jpg', N'Picture/source/d1.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/2.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Dual-color Bamboo Fan Red', 18, 28, 25, 1, N'Picture/2.jpg', N'Picture/source/d2.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/3.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Dual-color Bamboo Fan Blue', 17, 27, 39, 1, N'Picture/3.jpg', N'Picture/source/d3.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/4.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Dual-color Bamboo Fan Purple', 16, 26, 19, 1, N'Picture/4.jpg', N'Picture/source/d4.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/5.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Short Handle Painted Fan Plum', 18, 28, 29, 4, N'Picture/5.jpg', N'Picture/source/d5.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/6.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Short Handle Painted Fan Peach', 18, 28, 86, 5, N'Picture/6.jpg', N'Picture/source/d6.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/7.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 7', 19, 29, 42, 2, N'Picture/7.jpg', N'Picture/source/d7.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/8.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 8', 20, 30, 31, 2, N'Picture/8.jpg', N'Picture/source/d8.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/9.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 9', 21, 31, 22, 3, N'Picture/9.jpg', N'Picture/source/d9.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/10.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 10', 22, 32, 18, 3, N'Picture/10.jpg', N'Picture/source/d10.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/11.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 11', 23, 33, 27, 4, N'Picture/11.jpg', N'Picture/source/d11.jpg'); "
                + "IF NOT EXISTS (SELECT 1 FROM dbo.pruduct WHERE imagePath=N'Picture/12.jpg') "
                + "INSERT INTO dbo.pruduct(productName, price, originalPrice, soldCount, categoryId, imagePath, detailImagePath) VALUES "
                + "(N'Summer Fan 12', 24, 34, 36, 5, N'Picture/12.jpg', N'Picture/source/d12.jpg'); "
                + "IF OBJECT_ID(N'dbo.userinfo', N'U') IS NOT NULL "
                + "BEGIN "
                + "  IF EXISTS (SELECT 1 FROM dbo.userinfo WHERE userName = N'admin') "
                + "    UPDATE dbo.userinfo SET password = N'admin123', role = N'ADMIN', sex = ISNULL(NULLIF(sex, N''), N'未知'), interest = ISNULL(NULLIF(interest, N''), N'管理') WHERE userName = N'admin'; "
                + "  ELSE "
                + "    INSERT INTO dbo.userinfo(userName, password, sex, interest, role) VALUES (N'admin', N'admin123', N'未知', N'管理', N'ADMIN'); "
                + "END; ";
    }
}
