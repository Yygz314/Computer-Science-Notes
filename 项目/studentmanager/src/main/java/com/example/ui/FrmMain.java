package com.example.ui;

import com.example.bean.StudentBean;
import com.example.bean.UserBean;
import com.example.dao.StudentDao;
import com.example.ui.model.StudentTableModel;
import com.example.ui.tools.Calculator;
import com.example.ui.tools.Editor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FrmMain extends JFrame {

    private final UserBean         user;
    private List<StudentBean>      students;
    private final StudentTableModel tableModel;
    private final JTable           table;

    // 顶部快速搜索
    private final JTextField searchField = UITheme.createField("");
    private final JComboBox<String> searchType =
            new JComboBox<>(new String[]{"全部", "按学号", "按姓名", "按班级"});

    // 状态栏
    private final JLabel statusLabel = UITheme.createHintLabel("就绪");

    public FrmMain(UserBean user) {
        this.user      = user;
        this.students  = StudentDao.listAll();
        this.tableModel = new StudentTableModel(students);
        this.table      = buildTable();

        setTitle("学生成绩管理系统  ·  当前用户：" + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        buildUI();
        bindEvents();

        pack();
        setSize(1050, 680);
        setLocationRelativeTo(null);
        updateStatus();
    }

    // ════════════════════════════════════════════════════════
    //  UI 构建
    // ════════════════════════════════════════════════════════

    private void buildUI() {
        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.COLOR_BG);

        root.add(buildToolBar(),    BorderLayout.NORTH);
        root.add(buildTableArea(),  BorderLayout.CENTER);
        root.add(buildStatusBar(),  BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(UITheme.COLOR_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder());

        bar.add(buildMenu("学生管理", new Object[][]{
                {"添加学生", (Runnable) this::openAdd},
                {"编辑学生", (Runnable) this::openEdit},
                {"删除学生", (Runnable) this::deleteSelected},
                null,
                {"导入 Excel", (Runnable) this::importExcel},
                {"导出 Excel", (Runnable) this::exportExcel},
        }));

        bar.add(buildMenu("学生查询", new Object[][]{
                {"刷新全部",   (Runnable) this::loadAll},
                {"高级搜索",   (Runnable) this::openAdvancedSearch},
                {"成绩统计",   (Runnable) this::openStatistics},
        }));

        bar.add(buildMenu("系统设置", new Object[][]{
                {"修改密码", (Runnable) this::openChangePassword},
                {"切换用户", (Runnable) this::switchUser},
        }));

        bar.add(buildMenu("小工具", new Object[][]{
                {"计算器",     (Runnable) () -> new Calculator().setVisible(true)},
                {"文本编辑器", (Runnable) () -> new Editor().setVisible(true)},
        }));

        return bar;
    }

    /** 通用菜单构建：entries 为 Object[][]{text, Runnable} 或 null 表示分隔线 */
    private JMenu buildMenu(String title, Object[][] entries) {
        JMenu menu = new JMenu(title);
        menu.setFont(UITheme.FONT_MENU);
        menu.setForeground(UITheme.COLOR_TEXT);   // 黑色，Windows L&F 下可靠显示
        menu.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        for (Object[] entry : entries) {
            if (entry == null) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem((String) entry[0]);
                UITheme.styleMenuItem(item);
                Runnable action = (Runnable) entry[1];
                item.addActionListener(e -> action.run());
                menu.add(item);
            }
        }
        return menu;
    }

    private JPanel buildToolBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(UITheme.COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.COLOR_BORDER));

        // CRUD 按钮
        JButton btnAdd    = UITheme.createBtn("+ 添加");
        JButton btnEdit   = UITheme.createBtn("✎ 编辑");
        JButton btnDelete = UITheme.createDangerBtn("✕ 删除");
        JButton btnImport = UITheme.createBtn("↑ 导入");
        JButton btnExport = UITheme.createBtn("↓ 导出");

        btnAdd   .addActionListener(e -> openAdd());
        btnEdit  .addActionListener(e -> openEdit());
        btnDelete.addActionListener(e -> deleteSelected());
        btnImport.addActionListener(e -> importExcel());
        btnExport.addActionListener(e -> exportExcel());

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(makeSep());
        bar.add(btnImport);
        bar.add(btnExport);
        bar.add(makeSep());

        // 快速搜索
        searchType.setFont(UITheme.FONT_NORMAL);
        searchType.setPreferredSize(new Dimension(100, 34));
        searchField.setPreferredSize(new Dimension(200, 34));

        JButton btnSearch  = UITheme.createBtn("搜索");
        JButton btnRefresh = UITheme.createBtn("全部");
        btnRefresh.setBackground(new Color(100, 160, 100));
        btnRefresh.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRefresh.setBackground(new Color(70, 130, 70)); }
            public void mouseExited (MouseEvent e) { btnRefresh.setBackground(new Color(100, 160, 100)); }
        });

        btnSearch .addActionListener(e -> doQuickSearch());
        btnRefresh.addActionListener(e -> loadAll());

        searchField.addActionListener(e -> doQuickSearch()); // 回车搜索

        bar.add(UITheme.createLabel("搜索："));
        bar.add(searchType);
        bar.add(searchField);
        bar.add(btnSearch);
        bar.add(btnRefresh);

        return bar;
    }

    private JSeparator makeSep() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(UITheme.COLOR_BORDER);
        return sep;
    }

    private JScrollPane buildTableArea() {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sp.setBackground(UITheme.COLOR_BG);
        sp.getViewport().setBackground(UITheme.COLOR_PANEL);
        return sp;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        bar.setBackground(UITheme.COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.COLOR_BORDER));
        bar.add(statusLabel);
        return bar;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel);
        t.setFont(UITheme.FONT_TABLE);
        t.setRowHeight(36);
        t.setGridColor(UITheme.COLOR_BORDER);
        t.setBackground(UITheme.COLOR_PANEL);
        t.setSelectionBackground(UITheme.COLOR_PRIMARY_LIGHT);
        t.setSelectionForeground(UITheme.COLOR_TEXT);
        t.setShowGrid(true);
        t.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);  // 点击列头排序

        // 表头样式 — 自定义 renderer 确保背景和文字颜色在所有 L&F 下都正常渲染
        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, value, sel, focus, row, col);
                setText(value == null ? "" : value.toString());
                setFont(UITheme.FONT_HEADER);
                setForeground(UITheme.COLOR_TEXT);                      // 黑字
                setBackground(new Color(220, 230, 245));                // 浅蓝灰底
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.COLOR_PRIMARY),
                        BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                return this;
            }
        });

        // 列宽
        int[] widths = {80, 120, 70, 160, 80};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // 普通列：交替行色 + 居中
        RowRenderer rowRenderer = new RowRenderer();
        for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }
        // 分数列：带等级色
        t.getColumnModel().getColumn(4).setCellRenderer(new ScoreRenderer());

        // 右键菜单
        JPopupMenu popup = buildPopupMenu();
        t.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openEdit();
            }
            @Override public void mousePressed(MouseEvent e) {
                selectRowAt(t, e);
                if (e.isPopupTrigger()) popup.show(t, e.getX(), e.getY());
            }
            @Override public void mouseReleased(MouseEvent e) {
                selectRowAt(t, e);
                if (e.isPopupTrigger()) popup.show(t, e.getX(), e.getY());
            }
        });

        return t;
    }

    /** 交替行背景渲染器（居中） */
    private static class RowRenderer extends DefaultTableCellRenderer {
        private static final Color ROW_ODD  = Color.WHITE;
        private static final Color ROW_EVEN = new Color(245, 248, 253);

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected) {
                setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
            }
            return this;
        }
    }

    /** 分数列：按成绩段着色 */
    private static class ScoreRenderer extends DefaultTableCellRenderer {
        private static final Color CLR_EXCELLENT = new Color(195, 240, 210); // 优：绿
        private static final Color CLR_GOOD      = new Color(200, 222, 248); // 良：蓝
        private static final Color CLR_PASS      = new Color(255, 238, 180); // 合：黄
        private static final Color CLR_FAIL      = new Color(252, 205, 205); // 差：红

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value instanceof Integer) {
                int s = (Integer) value;
                if      (s >= 90) setBackground(CLR_EXCELLENT);
                else if (s >= 75) setBackground(CLR_GOOD);
                else if (s >= 60) setBackground(CLR_PASS);
                else              setBackground(CLR_FAIL);
            } else if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 253));
            }
            return this;
        }
    }

    /** 右键弹出菜单 */
    private JPopupMenu buildPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEdit   = new JMenuItem("✎ 编辑学生");
        JMenuItem miDelete = new JMenuItem("✕ 删除学生");
        JMenuItem miCopy   = new JMenuItem("⎘ 复制信息");
        for (JMenuItem mi : new JMenuItem[]{miEdit, miDelete, miCopy}) {
            mi.setFont(UITheme.FONT_NORMAL);
        }
        miEdit  .addActionListener(e -> openEdit());
        miDelete.addActionListener(e -> deleteSelected());
        miCopy  .addActionListener(e -> copySelectedInfo());
        popup.add(miEdit);
        popup.add(miDelete);
        popup.addSeparator();
        popup.add(miCopy);
        return popup;
    }

    /** 右键时先选中所点击的行 */
    private static void selectRowAt(JTable t, MouseEvent e) {
        int row = t.rowAtPoint(e.getPoint());
        if (row >= 0 && !t.isRowSelected(row)) {
            t.setRowSelectionInterval(row, row);
        }
    }

    /** 复制选中行的学生信息到剪切板 */
    private void copySelectedInfo() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int modelRow = table.convertRowIndexToModel(row);
        StudentBean s = tableModel.getStudent(modelRow);
        String info = String.format("学号：%d  姓名：%s  性别：%s  班级：%s  分数：%d",
                s.getID(), s.getName(), s.getGender(), s.getClazz(), s.getScore());
        java.awt.Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(info), null);
        statusLabel.setText("已复制：" + s.getName());
    }

    // ════════════════════════════════════════════════════════
    //  事件绑定 / 操作
    // ════════════════════════════════════════════════════════

    private void bindEvents() { /* 全部在 buildUI/buildToolBar 中直接绑定 */ }

    private void loadAll() {
        students = StudentDao.listAll();
        tableModel.setStudents(students);
        searchField.setText("");
        updateStatus();
    }

    private void doQuickSearch() {
        String kw   = searchField.getText().trim();
        String type = (String) searchType.getSelectedItem();
        if (kw.isEmpty() || "全部".equals(type)) {
            loadAll();
            return;
        }
        switch (type) {
            case "按学号": students = StudentDao.search(kw, null, null, null, null, null, false); break;
            case "按姓名": students = StudentDao.search(null, kw, null, null, null, null, true);  break;
            case "按班级": students = StudentDao.search(null, null, null, kw, null, null, true);  break;
            default:       students = StudentDao.listAll(); break;
        }
        tableModel.setStudents(students);
        updateStatus();
    }

    private void openAdd() {
        StudentAddDialog d = new StudentAddDialog(this);
        d.setVisible(true);
    }

    private void openEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要编辑的学生。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        StudentEditDialog d = new StudentEditDialog(this, tableModel.getStudent(modelRow));
        d.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的学生。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        StudentBean s = tableModel.getStudent(modelRow);
        int ans = JOptionPane.showConfirmDialog(this,
                "确认删除学生【" + s.getName() + "（" + s.getID() + "）】？",
                "删除确认", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ans != JOptionPane.YES_OPTION) return;
        StudentDao.delete(s);
        onDeleted(s);
    }

    private void openAdvancedSearch() {
        new AdvancedSearchDialog(this).setVisible(true);
    }

    private void openChangePassword() {
        new AdminChangePassword(this, user).setVisible(true);
    }

    private void switchUser() {
        int ans = JOptionPane.showConfirmDialog(this,
                "切换用户将关闭当前窗口并返回登录界面，确认？",
                "切换用户", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;
        dispose();
        StudentManagerLogin login = new StudentManagerLogin();
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
    }

    private void importExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("导入 Excel");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel 文件 (*.xlsx)", "xlsx"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        // 询问重复学号的处理策略
        int choice = JOptionPane.showOptionDialog(this,
                "导入时若遇到学号已存在的记录，如何处理？",
                "重复数据处理",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"覆盖原有数据", "跳过该行", "取消导入"},
                "跳过该行");
        if (choice == 2 || choice < 0) return;
        boolean overwrite = (choice == 0);

        File file = fc.getSelectedFile();
        int inserted = 0, updated = 0, skipped = 0;
        List<String> errorLines = new ArrayList<>();

        try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int r = 1; r <= lastRow; r++) {   // 第 0 行为表头，从第 1 行开始
                Row row = sheet.getRow(r);
                if (row == null || isRowBlank(row)) continue;

                try {
                    long   id     = cellLong(row.getCell(0));
                    String name   = cellStr (row.getCell(1));
                    String gender = cellStr (row.getCell(2));
                    String clazz  = cellStr (row.getCell(3));
                    int    score  = cellInt (row.getCell(4));

                    // 字段验证
                    if (id <= 0) {
                        errorLines.add("第 " + (r + 1) + " 行：学号无效（" + id + "）");
                        skipped++; continue;
                    }
                    if (name.isEmpty()) {
                        errorLines.add("第 " + (r + 1) + " 行：姓名为空");
                        skipped++; continue;
                    }
                    if (score < 0 || score > 150) {
                        errorLines.add("第 " + (r + 1) + " 行：分数超出范围（" + score + "）");
                        skipped++; continue;
                    }

                    if (StudentDao.exists(id)) {
                        if (!overwrite) { skipped++; continue; }
                        StudentBean s = new StudentBean(id, name, score, gender, clazz);
                        s.setUpdated(true);
                        StudentDao.update(s);
                        updated++;
                    } else {
                        StudentDao.insert(new StudentBean(id, name, score, gender, clazz));
                        inserted++;
                    }
                } catch (Exception ex) {
                    errorLines.add("第 " + (r + 1) + " 行：" + ex.getMessage());
                    skipped++;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "读取文件失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadAll();

        StringBuilder msg = new StringBuilder();
        msg.append(String.format("导入完成！\n新增 %d 条   更新 %d 条   跳过 %d 条",
                inserted, updated, skipped));
        if (!errorLines.isEmpty()) {
            msg.append("\n\n以下行存在问题：");
            for (String err : errorLines) msg.append("\n• ").append(err);
        }
        int msgType = errorLines.isEmpty()
                ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(this, msg.toString(), "导入结果", msgType);
    }

    // ── Excel 读取工具方法 ─────────────────────────────────

    private static boolean isRowBlank(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private static long cellLong(Cell cell) {
        if (cell == null) return -1;
        switch (cell.getCellType()) {
            case NUMERIC: return (long) cell.getNumericCellValue();
            case STRING:
                try { return Long.parseLong(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { return -1; }
            default: return -1;
        }
    }

    private static int cellInt(Cell cell) {
        if (cell == null) return -1;
        switch (cell.getCellType()) {
            case NUMERIC: return (int) cell.getNumericCellValue();
            case STRING:
                try { return Integer.parseInt(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { return -1; }
            default: return -1;
        }
    }

    private static String cellStr(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

    private void exportExcel() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "当前无数据可导出。", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("导出 Excel");
        fc.setSelectedFile(new File("学生成绩表_" + System.currentTimeMillis() + ".xlsx"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel 文件 (*.xlsx)", "xlsx"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) file = new File(file.getPath() + ".xlsx");

        try {
            writeExcel(file);
            JOptionPane.showMessageDialog(this,
                    "导出成功！\n路径：" + file.getAbsolutePath(), "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "导出失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeExcel(File file) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("学生成绩");
            sheet.setDefaultColumnWidth(16);

            // 标题样式
            CellStyle hStyle = wb.createCellStyle();
            Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setFontHeightInPoints((short) 14);
            hStyle.setFont(hFont);
            hStyle.setAlignment(HorizontalAlignment.CENTER);
            hStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorderThin(hStyle);

            // 内容样式
            CellStyle cStyle = wb.createCellStyle();
            cStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorderThin(cStyle);

            // 表头
            String[] cols = {"学号", "姓名", "性别", "班级", "分数"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(hStyle);
            }

            // 数据
            for (int r = 0; r < students.size(); r++) {
                StudentBean s = students.get(r);
                Row row = sheet.createRow(r + 1);
                Object[] vals = {s.getID(), s.getName(), s.getGender(), s.getClazz(), s.getScore()};
                for (int c = 0; c < vals.length; c++) {
                    Cell cell = row.createCell(c);
                    if (vals[c] instanceof Number) cell.setCellValue(((Number) vals[c]).doubleValue());
                    else cell.setCellValue(String.valueOf(vals[c]));
                    cell.setCellStyle(cStyle);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
    }

    private static void setBorderThin(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    // ════════════════════════════════════════════════════════
    //  表格回调（供对话框调用）
    // ════════════════════════════════════════════════════════

    void onAdded(StudentBean s) {
        int row = students.size();
        students.add(s);
        tableModel.fireTableRowsInserted(row, row);
        updateStatus();
    }

    void onUpdated(StudentBean s) {
        int row = students.indexOf(s);
        if (row >= 0) tableModel.fireTableRowsUpdated(row, row);
    }

    void onDeleted(StudentBean s) {
        int row = students.indexOf(s);
        students.remove(s);
        tableModel.fireTableRowsDeleted(row, row);
        updateStatus();
    }

    /** 高级搜索结果回显 */
    void applySearchResult(List<StudentBean> result) {
        students = result;
        tableModel.setStudents(students);
        updateStatus();
    }

    private void openStatistics() {
        new StatisticsDialog(this, students).setVisible(true);
    }

    private void updateStatus() {
        if (students.isEmpty()) {
            statusLabel.setText("共 0 条记录   |   当前用户：" + user.getUsername());
            return;
        }
        int sum = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (StudentBean s : students) {
            int sc = s.getScore();
            sum += sc;
            if (sc > max) max = sc;
            if (sc < min) min = sc;
        }
        double avg = (double) sum / students.size();
        statusLabel.setText(String.format(
                "共 %d 条记录   均分 %.1f   最高 %d   最低 %d   |   当前用户：%s",
                students.size(), avg, max, min, user.getUsername()));
    }
}
