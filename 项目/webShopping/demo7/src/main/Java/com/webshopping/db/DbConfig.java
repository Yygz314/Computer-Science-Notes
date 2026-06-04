package com.webshopping.db;

public final class DbConfig {
    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost:1433;databaseName=shopping;encrypt=true;trustServerCertificate=true;loginTimeout=5";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";

    private DbConfig() {
    }

    public static String driverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    public static String url() {
        return resolve("JSP_DB_URL", "jsp.db.url", DEFAULT_URL);
    }

    public static String username() {
        return resolve("JSP_DB_USER", "jsp.db.user", DEFAULT_USER);
    }

    public static String password() {
        return resolve("JSP_DB_PASSWORD", "jsp.db.password", DEFAULT_PASSWORD);
    }

    private static String resolve(String envKey, String sysPropKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        String propValue = System.getProperty(sysPropKey);
        if (propValue != null && !propValue.trim().isEmpty()) {
            return propValue.trim();
        }
        return defaultValue;
    }
}
