package com.example.bean;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class UserBean {
    private final String username;
    private String password;
    private boolean isUpdated;

    public UserBean(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
        this.isUpdated = true;
    }

    public String getUsername() {
        return username;
    }

    public static String hashPassword(String password) {
        // THIS IS NOT A SECURE PASSWORD HASH BUT IT'S FOR HOMEWORK SO LOL
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] hash = messageDigest.digest();
            BigInteger bigInteger = new BigInteger(1, hash); // for Java 8 hex encoding
            return String.format("%0" + (hash.length << 1) + "x", bigInteger);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ain't no way we have no SHA-1 support in JRE.", e);
        }
    }

    public boolean validatePassword(String password) {
        return Objects.equals(hashPassword(password), this.password);
    }

    public String getPasswordHash() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
        this.isUpdated = true;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }
}
