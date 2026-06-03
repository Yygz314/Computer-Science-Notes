package com.example.ui.tools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * 简易计算器
 * 布局：5 行 × 4 列等大按钮，显示区顶部，支持键盘输入与括号运算
 */
public class Calculator extends JFrame {

    // ── 配色（浅色主题，黑色文字）────────────────────────
    private static final Color BG         = new Color(240, 242, 246);
    private static final Color DISPLAY_BG = new Color(250, 251, 253);
    private static final Color BTN_NUM    = Color.WHITE;
    private static final Color BTN_NUM_H  = new Color(225, 230, 240);
    private static final Color BTN_OP     = new Color(218, 232, 248);
    private static final Color BTN_OP_H   = new Color(190, 212, 240);
    private static final Color BTN_CLR    = new Color(248, 220, 220);
    private static final Color BTN_CLR_H  = new Color(235, 190, 190);
    private static final Color BTN_EQ     = new Color(58, 110, 165);
    private static final Color BTN_EQ_H   = new Color(40,  85, 140);
    private static final Color BORDER_CLR = new Color(210, 215, 225);
    private static final Color TEXT       = Color.BLACK;
    private static final Color TEXT_EQ    = Color.BLACK;
    private static final Color TEXT_EXPR  = new Color(100, 110, 125);

    // ── 字体 ─────────────────────────────────────────────
    private static final Font FONT_RESULT = new Font("Consolas", Font.BOLD,  32);
    private static final Font FONT_EXPR   = new Font("Consolas", Font.PLAIN, 14);
    private static final Font FONT_BTN    = new Font("微软雅黑",  Font.PLAIN, 18);

    // ── 组件 ─────────────────────────────────────────────
    private final JLabel lblExpr   = new JLabel(" ", SwingConstants.RIGHT);
    private final JLabel lblResult = new JLabel("0",  SwingConstants.RIGHT);

    // ── 状态 ─────────────────────────────────────────────
    private String  expr       = "";
    private boolean justCalced = false;

    public Calculator() {
        setTitle("计算器");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        bindKeyboard();
        pack();
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════
    //  UI 构建
    // ════════════════════════════════════════════════════════

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.add(buildDisplay(), BorderLayout.NORTH);
        root.add(buildGrid(),    BorderLayout.CENTER);
        setContentPane(root);
    }

    /** 显示区：上方小字显示算式，下方大字显示当前数值 */
    private JPanel buildDisplay() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(DISPLAY_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(14, 16, 10, 16)));

        lblExpr.setFont(FONT_EXPR);
        lblExpr.setForeground(TEXT_EXPR);

        lblResult.setFont(FONT_RESULT);
        lblResult.setForeground(TEXT);

        p.add(lblExpr,   BorderLayout.NORTH);
        p.add(lblResult, BorderLayout.SOUTH);
        return p;
    }

    /**
     * 按钮区：5 行 × 4 列，全部等大
     *
     *  Row 0:  C    ←    ( )   ÷
     *  Row 1:  7    8    9     ×
     *  Row 2:  4    5    6     −
     *  Row 3:  1    2    3     +
     *  Row 4:  ±    0    .     =
     */
    private JPanel buildGrid() {
        JPanel p = new JPanel(new GridLayout(5, 4, 6, 6));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 行 0 — 功能键
        addBtn(p, "C",    BTN_CLR, BTN_CLR_H, TEXT, e -> clearAll());
        addBtn(p, "←",   BTN_CLR, BTN_CLR_H, TEXT, e -> backspace());
        addBtn(p, "( )",  BTN_OP,  BTN_OP_H,  TEXT, e -> appendParen());
        addBtn(p, "÷",   BTN_OP,  BTN_OP_H,  TEXT, e -> appendOp("/"));

        // 行 1
        addBtn(p, "7", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("7"));
        addBtn(p, "8", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("8"));
        addBtn(p, "9", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("9"));
        addBtn(p, "×", BTN_OP,  BTN_OP_H,  TEXT, e -> appendOp("*"));

        // 行 2
        addBtn(p, "4", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("4"));
        addBtn(p, "5", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("5"));
        addBtn(p, "6", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("6"));
        addBtn(p, "−", BTN_OP,  BTN_OP_H,  TEXT, e -> appendOp("-"));

        // 行 3
        addBtn(p, "1", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("1"));
        addBtn(p, "2", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("2"));
        addBtn(p, "3", BTN_NUM, BTN_NUM_H, TEXT, e -> appendDigit("3"));
        addBtn(p, "+", BTN_OP,  BTN_OP_H,  TEXT, e -> appendOp("+"));

        // 行 4
        addBtn(p, "±",  BTN_NUM, BTN_NUM_H, TEXT,    e -> toggleSign());
        addBtn(p, "0",  BTN_NUM, BTN_NUM_H, TEXT,    e -> appendDigit("0"));
        addBtn(p, ".",  BTN_NUM, BTN_NUM_H, TEXT,    e -> appendDot());
        addBtn(p, "=",  BTN_EQ,  BTN_EQ_H,  TEXT_EQ, e -> calculate());

        return p;
    }

    private void addBtn(JPanel panel, String label,
                        Color bg, Color hover, Color fg,
                        ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(72, 54));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        btn.addActionListener(action);
        panel.add(btn);
    }

    // ════════════════════════════════════════════════════════
    //  逻辑
    // ════════════════════════════════════════════════════════

    private void appendDigit(String d) {
        if (justCalced) { expr = ""; justCalced = false; }
        expr += d;
        refreshDisplay();
    }

    private void appendOp(String op) {
        justCalced = false;
        if (!expr.isEmpty()) {
            char last = expr.charAt(expr.length() - 1);
            if ("+-*/".indexOf(last) >= 0) {
                expr = expr.substring(0, expr.length() - 1);
            }
        }
        expr += op;
        refreshDisplay();
    }

    private void appendDot() {
        if (justCalced) { expr = "0"; justCalced = false; }
        if (expr.isEmpty()) expr = "0";
        // 当前数字段已有小数点则不加
        int i = expr.length() - 1;
        while (i >= 0 && "+-*/(". indexOf(expr.charAt(i)) < 0) i--;
        if (!expr.substring(i + 1).contains(".")) expr += ".";
        refreshDisplay();
    }

    private void appendParen() {
        justCalced = false;
        long open  = expr.chars().filter(c -> c == '(').count();
        long close = expr.chars().filter(c -> c == ')').count();
        boolean addOpen = open == close || expr.isEmpty()
                || "+-*/(".indexOf(expr.charAt(expr.length() - 1)) >= 0;
        expr += addOpen ? "(" : ")";
        refreshDisplay();
    }

    private void toggleSign() {
        if (expr.isEmpty() || expr.equals("0")) return;
        expr = expr.startsWith("-") ? expr.substring(1) : "-" + expr;
        refreshDisplay();
    }

    private void backspace() {
        justCalced = false;
        if (!expr.isEmpty()) expr = expr.substring(0, expr.length() - 1);
        refreshDisplay();
    }

    private void clearAll() {
        expr = ""; justCalced = false;
        lblExpr.setText(" ");
        lblResult.setText("0");
    }

    private void calculate() {
        if (expr.isEmpty()) return;
        String snapshot = expr;
        try {
            double val = eval(snapshot);
            String resultStr = isWhole(val)
                    ? String.valueOf((long) val)
                    : String.format("%.10f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
            lblExpr.setText(snapshot + " =");
            lblResult.setText(resultStr);
            expr = resultStr;
            justCalced = true;
        } catch (ArithmeticException ex) {
            lblResult.setText("除以零");
            expr = ""; justCalced = true;
        } catch (Exception ex) {
            lblResult.setText("错误");
            expr = ""; justCalced = true;
        }
    }

    private void refreshDisplay() {
        lblResult.setText(expr.isEmpty() ? "0" : expr);
    }

    private static boolean isWhole(double v) {
        return !Double.isInfinite(v) && !Double.isNaN(v) && v == Math.floor(v);
    }

    // ════════════════════════════════════════════════════════
    //  递归下降求值
    // ════════════════════════════════════════════════════════

    private int pos;
    private String src;

    private double eval(String expression) {
        src = expression.replace(" ", "")
                .replace("×", "*").replace("÷", "/").replace("−", "-");
        pos = 0;
        double result = parseExpr();
        if (pos != src.length())
            throw new RuntimeException("未预期字符：" + src.charAt(pos));
        return result;
    }

    private double parseExpr() {
        double v = parseTerm();
        while (pos < src.length()) {
            char op = src.charAt(pos);
            if (op != '+' && op != '-') break;
            pos++;
            double r = parseTerm();
            v = op == '+' ? v + r : v - r;
        }
        return v;
    }

    private double parseTerm() {
        double v = parseFactor();
        while (pos < src.length()) {
            char op = src.charAt(pos);
            if (op != '*' && op != '/' && op != '%') break;
            pos++;
            double r = parseFactor();
            if (op == '/') {
                if (r == 0) throw new ArithmeticException("除以零");
                v /= r;
            } else if (op == '%') {
                v %= r;
            } else {
                v *= r;
            }
        }
        return v;
    }

    private double parseFactor() {
        if (pos < src.length() && src.charAt(pos) == '(') {
            pos++;
            double v = parseExpr();
            if (pos < src.length() && src.charAt(pos) == ')') pos++;
            return v;
        }
        if (pos < src.length() && src.charAt(pos) == '-') {
            pos++;
            return -parseFactor();
        }
        return parseNumber();
    }

    private double parseNumber() {
        int start = pos;
        while (pos < src.length()
                && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) {
            pos++;
        }
        if (start == pos) throw new RuntimeException("期望数字，位置：" + pos);
        return Double.parseDouble(src.substring(start, pos));
    }

    // ════════════════════════════════════════════════════════
    //  键盘支持
    // ════════════════════════════════════════════════════════

    private void bindKeyboard() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char ch   = e.getKeyChar();
                int  code = e.getKeyCode();
                if (Character.isDigit(ch))               appendDigit(String.valueOf(ch));
                else if (ch == '+')                      appendOp("+");
                else if (ch == '-')                      appendOp("-");
                else if (ch == '*')                      appendOp("*");
                else if (ch == '/')                      appendOp("/");
                else if (ch == '%')                      appendOp("%");
                else if (ch == '.')                      appendDot();
                else if (ch == '(' || ch == ')')         appendParen();
                else if (code == KeyEvent.VK_ENTER)      calculate();
                else if (code == KeyEvent.VK_BACK_SPACE) backspace();
                else if (code == KeyEvent.VK_ESCAPE)     clearAll();
            }
        });
        setFocusable(true);
    }
}
