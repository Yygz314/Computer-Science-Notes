package com.example.dao;

import com.example.Database;
import com.example.bean.StudentBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentDao {

    // ── 查询 ──────────────────────────────────────────────

    public static List<StudentBean> listAll() {
        List<StudentBean> list = new ArrayList<>();
        try (Statement s = Database.getConnection().createStatement()) {
            load(s.executeQuery("SELECT * FROM students"), list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * 灵活查询：所有参数均可为 null / 空字符串表示忽略
     * @param fuzzy 为 true 时姓名/班级使用 LIKE 模糊匹配
     */
    public static List<StudentBean> search(String id, String name, String gender,
                                           String clazz, Integer scoreMin, Integer scoreMax,
                                           boolean fuzzy) {
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (id != null && !id.isEmpty()) {
            sql.append(" AND CAST(id AS TEXT) LIKE ?");
            params.add("%" + id + "%");
        }
        if (name != null && !name.isEmpty()) {
            sql.append(fuzzy ? " AND name LIKE ?" : " AND name = ?");
            params.add(fuzzy ? "%" + name + "%" : name);
        }
        if (gender != null && !gender.isEmpty()) {
            sql.append(" AND gender = ?");
            params.add(gender);
        }
        if (clazz != null && !clazz.isEmpty()) {
            sql.append(fuzzy ? " AND clazz LIKE ?" : " AND clazz = ?");
            params.add(fuzzy ? "%" + clazz + "%" : clazz);
        }
        if (scoreMin != null) {
            sql.append(" AND score >= ?");
            params.add(scoreMin);
        }
        if (scoreMax != null) {
            sql.append(" AND score <= ?");
            params.add(scoreMax);
        }

        List<StudentBean> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            load(ps.executeQuery(), list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── 增删改 ────────────────────────────────────────────

    public static void insert(StudentBean s) {
        // 若已持有用户指定的 ID，则显式写入；否则让数据库自动生成
        boolean hasId = s.getID() >= 0;
        String sql = hasId
                ? "INSERT INTO students (id, name, score, gender, clazz) VALUES (?,?,?,?,?)"
                : "INSERT INTO students (name, score, gender, clazz) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            if (hasId) {
                ps.setLong(1, s.getID());
                ps.setString(2, s.getName());
                ps.setInt(3, s.getScore());
                ps.setString(4, s.getGender());
                ps.setString(5, s.getClazz());
            } else {
                ps.setString(1, s.getName());
                ps.setInt(2, s.getScore());
                ps.setString(3, s.getGender());
                ps.setString(4, s.getClazz());
            }
            ps.executeUpdate();
            if (!hasId) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) s.setID(rs.getLong(1));
                }
            }
            s.setUpdated(false);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** 检查指定学号是否已存在 */
    public static boolean exists(long id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM students WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public static void update(StudentBean s) {
        update(Collections.singletonList(s));
    }

    public static void update(Iterable<StudentBean> students) {
        String sql = "UPDATE students SET name=?, score=?, gender=?, clazz=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            for (StudentBean s : students) {
                if (!s.isUpdated()) continue;
                ps.setString(1, s.getName());
                ps.setInt(2, s.getScore());
                ps.setString(3, s.getGender());
                ps.setString(4, s.getClazz());
                ps.setLong(5, s.getID());
                ps.addBatch();
                s.setUpdated(false);
            }
            ps.executeBatch();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void delete(StudentBean s) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "DELETE FROM students WHERE id=?")) {
            ps.setLong(1, s.getID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── 工具 ─────────────────────────────────────────────

    private static void load(ResultSet rs, List<StudentBean> out) throws SQLException {
        while (rs.next()) {
            out.add(new StudentBean(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getInt("score"),
                    rs.getString("gender"),
                    rs.getString("clazz")
            ));
        }
    }

    // ── 向后兼容的旧方法名（供遗留代码调用） ──────────────────

    public static List<StudentBean> listAllStudents() { return listAll(); }

    public static List<StudentBean> listStudentsByID(long id) {
        return search(String.valueOf(id), null, null, null, null, null, false);
    }

    public static List<StudentBean> listStudentsByName(String name) {
        return search(null, name, null, null, null, null, false);
    }

    public static List<StudentBean> listStudentsByScoreRange(int from, int to) {
        return search(null, null, null, null, from, to, false);
    }
}
