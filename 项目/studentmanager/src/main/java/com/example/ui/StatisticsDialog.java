package com.example.ui;

import com.example.bean.StudentBean;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 成绩统计分析对话框
 * 展示：总体统计、等级分布、各班均分
 */
public class StatisticsDialog extends JDialog {

    private final List<StudentBean> students;

    public StatisticsDialog(Window owner, List<StudentBean> students) {
        super(owner, "成绩统计分析", ModalityType.APPLICATION_MODAL);
        this.students = students;
        setResizable(false);
        buildUI();
        pack();
        setMinimumSize(new Dimension(520, 480));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        // 标题栏
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(UITheme.COLOR_PRIMARY);
        JLabel title = new JLabel("成绩统计分析");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        header.add(title);

        // 内容区
        JPanel content = new JPanel();
        content.setBackground(UITheme.COLOR_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(14, 16, 14, 16));

        if (students.isEmpty()) {
            JLabel empty = UITheme.createLabel("暂无数据");
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(empty);
        } else {
            content.add(buildOverallPanel());
            content.add(Box.createVerticalStrut(12));
            content.add(buildGradePanel());
            content.add(Box.createVerticalStrut(12));
            content.add(buildClassPanel());
        }

        // 关闭按钮
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setBackground(UITheme.COLOR_BG);
        JButton btnClose = UITheme.createBtn("关  闭");
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header,  BorderLayout.NORTH);
        root.add(new JScrollPane(content) {{
            setBorder(BorderFactory.createEmptyBorder());
            setBackground(UITheme.COLOR_BG);
        }}, BorderLayout.CENTER);
        root.add(footer,  BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── 总体统计卡片 ──────────────────────────────────────

    private JPanel buildOverallPanel() {
        int n = students.size();
        int sum = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (StudentBean s : students) {
            int sc = s.getScore();
            sum += sc; if (sc > max) max = sc; if (sc < min) min = sc;
        }
        double avg = (double) sum / n;

        JPanel card = card("总体概况");
        JPanel grid = new JPanel(new GridLayout(1, 4, 10, 0));
        grid.setBackground(UITheme.COLOR_PANEL);
        grid.add(statBox("总人数", String.valueOf(n),             new Color(58, 110, 165)));
        grid.add(statBox("平均分", String.format("%.1f", avg),   new Color(70, 150, 100)));
        grid.add(statBox("最高分", String.valueOf(max),           new Color(200, 130, 40)));
        grid.add(statBox("最低分", String.valueOf(min),           new Color(180, 60, 60)));
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    /** 单个数值框 */
    private JPanel statBox(String label, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(UITheme.COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                new EmptyBorder(10, 6, 10, 6)));

        JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
        valLbl.setFont(new Font("Consolas", Font.BOLD, 26));
        valLbl.setForeground(accent);

        JLabel nameLbl = new JLabel(label, SwingConstants.CENTER);
        nameLbl.setFont(UITheme.FONT_NORMAL);
        nameLbl.setForeground(UITheme.COLOR_TEXT_HINT);

        p.add(valLbl,  BorderLayout.CENTER);
        p.add(nameLbl, BorderLayout.SOUTH);
        return p;
    }

    // ── 等级分布卡片 ──────────────────────────────────────

    private JPanel buildGradePanel() {
        int excellent = 0, good = 0, pass = 0, fail = 0;
        for (StudentBean s : students) {
            int sc = s.getScore();
            if      (sc >= 90) excellent++;
            else if (sc >= 75) good++;
            else if (sc >= 60) pass++;
            else               fail++;
        }
        int n = students.size();

        JPanel card = card("等级分布");

        String[] cols = {"等级", "分数段", "人数", "占比"};
        Object[][] rows = {
            {"优秀", "90 ~ 100", excellent, pct(excellent, n)},
            {"良好", "75 ~ 89",  good,      pct(good, n)},
            {"合格", "60 ~ 74",  pass,      pct(pass, n)},
            {"不合格","0 ~ 59",  fail,      pct(fail, n)},
        };
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = styledTable(model);

        // 为等级列着色
        t.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private final Color[] COLORS = {
                new Color(195, 240, 210), new Color(200, 222, 248),
                new Color(255, 238, 180), new Color(252, 205, 205)
            };
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel) setBackground(COLORS[row % COLORS.length]);
                return this;
            }
        });

        card.add(new JScrollPane(t) {{
            setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER));
            setPreferredSize(new Dimension(0, t.getRowHeight() * 5 + 4));
        }}, BorderLayout.CENTER);
        return card;
    }

    // ── 各班均分卡片 ──────────────────────────────────────

    private JPanel buildClassPanel() {
        // clazz → (sum, count)
        Map<String, int[]> map = new LinkedHashMap<>();
        for (StudentBean s : students) {
            String cls = s.getClazz().isEmpty() ? "（未填写）" : s.getClazz();
            map.computeIfAbsent(cls, k -> new int[2]);
            map.get(cls)[0] += s.getScore();
            map.get(cls)[1]++;
        }

        // 按均分降序排列
        List<Map.Entry<String, int[]>> entries = new ArrayList<>(map.entrySet());
        entries.sort((a, b) -> Double.compare(
                (double) b.getValue()[0] / b.getValue()[1],
                (double) a.getValue()[0] / a.getValue()[1]));

        String[] cols = {"班级", "人数", "均分", "最高", "最低"};
        Object[][] rows = new Object[entries.size()][5];
        for (int i = 0; i < entries.size(); i++) {
            String cls     = entries.get(i).getKey();
            int[]  sc      = entries.get(i).getValue();
            double avg     = (double) sc[0] / sc[1];
            int    clsMax  = 0, clsMin = 150;
            for (StudentBean s : students) {
                String c = s.getClazz().isEmpty() ? "（未填写）" : s.getClazz();
                if (c.equals(cls)) {
                    if (s.getScore() > clsMax) clsMax = s.getScore();
                    if (s.getScore() < clsMin) clsMin = s.getScore();
                }
            }
            rows[i] = new Object[]{cls, sc[1], String.format("%.1f", avg), clsMax, clsMin};
        }

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = styledTable(model);

        JPanel card = card("各班成绩");
        int height = Math.min(entries.size(), 6) * t.getRowHeight() + 26;
        card.add(new JScrollPane(t) {{
            setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER));
            setPreferredSize(new Dimension(0, height));
        }}, BorderLayout.CENTER);
        return card;
    }

    // ── 工具 ──────────────────────────────────────────────

    /** 带标题边框的卡片 */
    private JPanel card(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UITheme.COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UITheme.COLOR_BORDER),
                        "  " + title + "  ",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        UITheme.FONT_LABEL,
                        UITheme.COLOR_PRIMARY),
                new EmptyBorder(8, 10, 10, 10)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    /** 通用样式表格 */
    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(UITheme.FONT_TABLE);
        t.setRowHeight(30);
        t.setGridColor(UITheme.COLOR_BORDER);
        t.setBackground(UITheme.COLOR_PANEL);
        t.setSelectionBackground(UITheme.COLOR_PRIMARY_LIGHT);
        t.setSelectionForeground(UITheme.COLOR_TEXT);
        t.setShowGrid(true);
        t.setEnabled(false);     // 只读展示

        // 自定义表头 renderer —— 确保黑字在任何 L&F 下都可见
        t.getTableHeader().setPreferredSize(new Dimension(0, 32));
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, sel, focus, row, col);
                setText(value == null ? "" : value.toString());
                setFont(UITheme.FONT_LABEL);
                setForeground(UITheme.COLOR_TEXT);               // 黑字
                setBackground(new Color(220, 230, 245));         // 浅蓝灰底
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.COLOR_PRIMARY),
                        BorderFactory.createEmptyBorder(3, 6, 3, 6)));
                return this;
            }
        });

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return t;
    }

    private static String pct(int part, int total) {
        return total == 0 ? "0%" : String.format("%.1f%%", 100.0 * part / total);
    }
}
