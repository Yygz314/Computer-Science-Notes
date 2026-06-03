package com.example.bean;

import java.util.Objects;

public class StudentBean {
    private long   id;
    private String name;
    private int    score;
    private String gender;  // "男" / "女" / ""
    private String clazz;   // 班级，如 "计算机2301"

    private boolean updated;

    public StudentBean(long id, String name, int score, String gender, String clazz) {
        this.id      = id;
        this.name    = name;
        this.score   = score;
        this.gender  = gender  != null ? gender  : "";
        this.clazz   = clazz   != null ? clazz   : "";
        this.updated = false;
    }

    /** 新建（尚无 ID）时使用 */
    public StudentBean(String name, int score, String gender, String clazz) {
        this(-1, name, score, gender, clazz);
        this.updated = true;
    }

    // ── ID ──
    public long getID() { return id; }

    public void setID(long id) {
        if (this.id >= 0) throw new IllegalStateException("ID 不可变");
        this.id = id;
    }

    // ── name ──
    public String getName() { return name; }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) { this.name = name; updated = true; }
    }

    // ── score ──
    public int getScore() { return score; }

    public void setScore(int score) {
        if (this.score != score) { this.score = score; updated = true; }
    }

    // ── gender ──
    public String getGender() { return gender; }

    public void setGender(String gender) {
        String g = gender != null ? gender : "";
        if (!Objects.equals(this.gender, g)) { this.gender = g; updated = true; }
    }

    // ── clazz ──
    public String getClazz() { return clazz; }

    public void setClazz(String clazz) {
        String c = clazz != null ? clazz : "";
        if (!Objects.equals(this.clazz, c)) { this.clazz = c; updated = true; }
    }

    // ── update flag ──
    public boolean isUpdated() { return updated; }

    public void setUpdated(boolean updated) { this.updated = updated; }
}
