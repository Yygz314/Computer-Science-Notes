package com.example.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 统一 UI 样式工具类
 */
public class UITheme {

    // ── 主色调：钢蓝 ──────────────────────────────────────
    public static final Color COLOR_PRIMARY       = new Color(58, 110, 165);
    public static final Color COLOR_PRIMARY_DARK  = new Color(40,  82, 130);
    public static final Color COLOR_PRIMARY_LIGHT = new Color(200, 220, 245);

    // ── 危险色：红 ────────────────────────────────────────
    public static final Color COLOR_DANGER        = new Color(195, 60,  60);
    public static final Color COLOR_DANGER_DARK   = new Color(155, 35,  35);

    // ── 背景 / 边框 ───────────────────────────────────────
    public static final Color COLOR_BG     = new Color(243, 246, 250);
    public static final Color COLOR_PANEL  = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(200, 210, 220);

    // ── 文字：全部黑色 ────────────────────────────────────
    public static final Color COLOR_TEXT      = Color.BLACK;
    public static final Color COLOR_TEXT_HINT = new Color(80, 80, 80);   // 次要文字：深灰

    // ── 字体 ──────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("微软雅黑", Font.BOLD,  28);
    public static final Font FONT_HEADER = new Font("微软雅黑", Font.BOLD,  18);
    public static final Font FONT_LABEL  = new Font("微软雅黑", Font.BOLD,  16);
    public static final Font FONT_NORMAL = new Font("微软雅黑", Font.PLAIN, 15);
    public static final Font FONT_BTN    = new Font("微软雅黑", Font.PLAIN, 16);
    public static final Font FONT_TABLE  = new Font("微软雅黑", Font.PLAIN, 15);
    public static final Font FONT_MENU   = new Font("微软雅黑", Font.PLAIN, 16);

    // ── 边框 ──────────────────────────────────────────────
    public static final Border BORDER_FIELD = BorderFactory.createLineBorder(COLOR_BORDER, 1);
    public static final Border BORDER_FOCUS = BorderFactory.createLineBorder(COLOR_PRIMARY, 2);

    // ─── 工厂方法 ──────────────────────────────────────────

    /** 主色按钮（白字蓝底） */
    public static JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_PRIMARY_DARK); }
            public void mouseExited (MouseEvent e) { btn.setBackground(COLOR_PRIMARY); }
        });
        return btn;
    }

    /** 危险按钮（白字红底） */
    public static JButton createDangerBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_DANGER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_DANGER_DARK); }
            public void mouseExited (MouseEvent e) { btn.setBackground(COLOR_DANGER); }
        });
        return btn;
    }

    /** 输入框（黑字白底，聚焦时蓝色边框） */
    public static JTextField createField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_NORMAL);
        tf.setForeground(COLOR_TEXT);
        tf.setBackground(COLOR_PANEL);
        tf.setBorder(compound(BORDER_FIELD));
        tf.setPreferredSize(new Dimension(200, 36));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { tf.setBorder(compound(BORDER_FOCUS)); }
            public void focusLost (FocusEvent e) { tf.setBorder(compound(BORDER_FIELD)); }
        });
        return tf;
    }

    /** 密码框（黑字白底，聚焦蓝框） */
    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_NORMAL);
        pf.setForeground(COLOR_TEXT);
        pf.setBackground(COLOR_PANEL);
        pf.setBorder(compound(BORDER_FIELD));
        pf.setPreferredSize(new Dimension(200, 36));
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { pf.setBorder(compound(BORDER_FOCUS)); }
            public void focusLost (FocusEvent e) { pf.setBorder(compound(BORDER_FIELD)); }
        });
        return pf;
    }

    /** 黑色粗体标签 */
    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    /** 深灰小字标签（次要提示） */
    public static JLabel createHintLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT_HINT);
        return lbl;
    }

    public static void styleMenuItem(JMenuItem item) {
        item.setFont(FONT_MENU);
        item.setForeground(COLOR_TEXT);
    }

    /** 全局外观配置：系统风格 + 黑色文字 */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // 强制全局文字为黑色
        UIManager.put("Label.foreground",           Color.BLACK);
        UIManager.put("TextField.foreground",       Color.BLACK);
        UIManager.put("TextArea.foreground",        Color.BLACK);
        UIManager.put("PasswordField.foreground",   Color.BLACK);
        UIManager.put("ComboBox.foreground",        Color.BLACK);
        UIManager.put("CheckBox.foreground",        Color.BLACK);
        UIManager.put("RadioButton.foreground",     Color.BLACK);
        UIManager.put("Spinner.foreground",         Color.BLACK);
        UIManager.put("Table.foreground",           Color.BLACK);
        UIManager.put("TableHeader.foreground",     Color.BLACK);  // 表头黑字
        UIManager.put("Menu.foreground",            Color.BLACK);  // 菜单栏黑字
        UIManager.put("MenuItem.foreground",        Color.BLACK);
        UIManager.put("List.foreground",            Color.BLACK);
        UIManager.put("TitledBorder.titleColor",    Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.BLACK);
    }

    // ── 内部工具 ──────────────────────────────────────────

    private static Border compound(Border outer) {
        return BorderFactory.createCompoundBorder(outer,
                BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }
}
