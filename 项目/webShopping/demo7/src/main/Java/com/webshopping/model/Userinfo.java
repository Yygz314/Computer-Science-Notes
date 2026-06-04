package com.webshopping.model;

import java.io.Serializable;

public class Userinfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String pwd;
    private String sex;
    private String hobby;
    private String role;

    public Userinfo() {
    }

    public Userinfo(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getRole() {
        return role == null ? "USER" : role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRole());
    }
}

