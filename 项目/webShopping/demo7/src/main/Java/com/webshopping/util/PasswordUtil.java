package com.webshopping.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(10));
    }

    public static boolean verify(String plainText, String storedValue) {
        if (plainText == null || storedValue == null || storedValue.trim().isEmpty()) {
            return false;
        }
        if (looksLikeBcrypt(storedValue)) {
            try {
                return BCrypt.checkpw(plainText, storedValue);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return storedValue.equals(plainText);
    }

    public static boolean shouldUpgradeHash(String storedValue) {
        return !looksLikeBcrypt(storedValue);
    }

    private static boolean looksLikeBcrypt(String value) {
        return value.startsWith("$2a$")
                || value.startsWith("$2b$")
                || value.startsWith("$2y$");
    }
}
