package com.example.ui.tools;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * 文本编辑器 —— 支持新建/打开/保存/另存为、查找替换、字体调整、撤销重做、字数统计
 */
public class Editor extends JFrame {

    // ── 配色 ──────────────────────────────────────────────
    private static final Color CLR_BG      = new Color(248, 249, 252);
    private static final Color CLR_TOOLBAR = new Color(245, 246, 250);
    private static final Color CLR_BORDER  = new Color(210, 215, 225);
    private static final Color CLR_STATUS  = new Color(235, 237, 242);
    private static final Color CLR_TEXT    = new Color(25,  25,  30);
    private static final Color CLR_LINE_BG = new Color(240, 242, 246);
    private static final Color CLR_PRIMARY = new Color(58, 110, 165);
    private static final Color HIGHLIGHT   = new Color(255, 220, 80, 180);

    // ── 核心组件 ─────────────────────────────────────────
    private final JTextArea    textArea   = new JTextArea();
    private final UndoManager  undoMgr    = new UndoManager();
    private final JLabel       statusLabel = new JLabel();
    private final JLabel       caretLabel  = new JLabel();

    // ── 查找替换状态 ──────────────────────────────────────
    private String  lastSearch  = "";
    private int     lastFindPos = 0;

    // ── 文件状态 ──────────────────────────────────────────
    private File    currentFile    = null;
    private boolean modified       = false;

    public Editor() {
        setTitle("文本编辑器");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 550));
        buildUI();
        bindEvents();
        pack();
        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════
    //  UI 构建
    // ════════════════════════════════════════════════════════

    private void buildUI() {
        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_BG);
        root.add(buildToolBar(),   BorderLayout.NORTH);
        root.add(buildTextArea(),  BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(CLR_TOOLBAR);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER));

        bar.add(buildMenu("文件(F)", new Object[][]{
                {"新建\tCtrl+N",   (Runnable) this::newFile},
                {"打开…\tCtrl+O",  (Runnable) this::openFile},
                null,
                {"保存\tCtrl+S",   (Runnable) this::save},
                {"另存为…",        (Runnable) this::saveAs},
        }));
        bar.add(buildMenu("编辑(E)", new Object[][]{
                {"撤销\tCtrl+Z",    (Runnable) () -> { if (undoMgr.canUndo()) undoMgr.undo(); }},
                {"重做\tCtrl+Y",    (Runnable) () -> { if (undoMgr.canRedo()) undoMgr.redo(); }},
                null,
                {"剪切\tCtrl+X",   (Runnable) textArea::cut},
                {"复制\tCtrl+C",   (Runnable) textArea::copy},
                {"粘贴\tCtrl+V",   (Runnable) textArea::paste},
                {"全选\tCtrl+A",   (Runnable) textArea::selectAll},
                null,
                {"查找/替换…\tCtrl+H", (Runnable) this::openFindReplace},
        }));
        bar.add(buildMenu("格式(O)", new Object[][]{
                {"字体设置…",          (Runnable) this::openFontChooser},
                {"自动换行",           (Runnable) this::toggleWrap},
        }));
        return bar;
    }

    private JMenu buildMenu(String title, Object[][] entries) {
        JMenu menu = new JMenu(title);
        menu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        for (Object[] e : entries) {
            if (e == null) { menu.addSeparator(); continue; }
            String[] parts = ((String) e[0]).split("\t");
            JMenuItem item = new JMenuItem(parts[0]);
            if (parts.length > 1) item.setAccelerator(parseShortcut(parts[1]));
            item.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            Runnable r = (Runnable) e[1];
            item.addActionListener(ev -> r.run());
            menu.add(item);
        }
        return menu;
    }

    private KeyStroke parseShortcut(String s) {
        try { return KeyStroke.getKeyStroke(s.replace("Ctrl+", "control ")); }
        catch (Exception e) { return null; }
    }

    private JPanel buildToolBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        bar.setBackground(CLR_TOOLBAR);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER));

        bar.add(toolBtn("新建",  "Ctrl+N", this::newFile));
        bar.add(toolBtn("打开",  "Ctrl+O", this::openFile));
        bar.add(toolBtn("保存",  "Ctrl+S", this::save));
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{ setPreferredSize(new Dimension(1, 26)); }});
        bar.add(toolBtn("撤销", "Ctrl+Z", () -> { if (undoMgr.canUndo()) undoMgr.undo(); }));
        bar.add(toolBtn("重做", "Ctrl+Y", () -> { if (undoMgr.canRedo()) undoMgr.redo(); }));
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{ setPreferredSize(new Dimension(1, 26)); }});
        bar.add(toolBtn("查找/替换", "Ctrl+H", this::openFindReplace));
        bar.add(toolBtn("字体",     null,      this::openFontChooser));
        return bar;
    }

    private JButton toolBtn(String text, String tooltip, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        btn.setForeground(CLR_TEXT);
        btn.setBackground(CLR_TOOLBAR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (tooltip != null) btn.setToolTipText(tooltip);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(220, 228, 240)); }
            public void mouseExited (MouseEvent e) { btn.setBackground(CLR_TOOLBAR); }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JScrollPane buildTextArea() {
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        textArea.setForeground(CLR_TEXT);
        textArea.setBackground(Color.WHITE);
        textArea.setCaretColor(CLR_PRIMARY);
        textArea.setMargin(new Insets(8, 12, 8, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getDocument().addUndoableEditListener(undoMgr);

        JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(CLR_BG);

        // 行号面板
        sp.setRowHeaderView(new LineNumberPanel(textArea));
        return sp;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CLR_STATUS);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CLR_BORDER));
        bar.setPreferredSize(new Dimension(0, 26));

        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 110, 130));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        caretLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        caretLabel.setForeground(new Color(100, 110, 130));
        caretLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(caretLabel,  BorderLayout.EAST);
        updateStatus();
        return bar;
    }

    // ════════════════════════════════════════════════════════
    //  事件绑定
    // ════════════════════════════════════════════════════════

    private void bindEvents() {
        // 窗口关闭确认
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (confirmSave()) dispose();
            }
        });

        // 文字变化 → 标记 modified + 刷新状态栏
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            public void changedUpdate(DocumentEvent e) { onTextChanged(); }
        });

        // 光标移动 → 更新行列
        textArea.addCaretListener(e -> updateCaret());

        // 快捷键（文本区直接捕获）
        textArea.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_N: e.consume(); newFile();        break;
                        case KeyEvent.VK_O: e.consume(); openFile();       break;
                        case KeyEvent.VK_S: e.consume(); save();           break;
                        case KeyEvent.VK_H: e.consume(); openFindReplace(); break;
                    }
                }
            }
        });
    }

    private void onTextChanged() {
        modified = true;
        updateStatus();
    }

    private void updateStatus() {
        String txt = textArea.getText();
        int chars = txt.length();
        int words = txt.trim().isEmpty() ? 0 : txt.trim().split("\\s+").length;
        int lines = textArea.getLineCount();
        statusLabel.setText(String.format("  字符：%d   字数：%d   行数：%d%s",
                chars, words, lines, modified ? "   [已修改]" : ""));
    }

    private void updateCaret() {
        try {
            int pos  = textArea.getCaretPosition();
            int line = textArea.getLineOfOffset(pos);
            int col  = pos - textArea.getLineStartOffset(line);
            caretLabel.setText(String.format("行 %d，列 %d  ", line + 1, col + 1));
        } catch (BadLocationException ignored) {}
    }

    // ════════════════════════════════════════════════════════
    //  文件操作
    // ════════════════════════════════════════════════════════

    private void newFile() {
        if (!confirmSave()) return;
        textArea.setText("");
        currentFile = null;
        modified    = false;
        setTitle("文本编辑器");
        undoMgr.discardAllEdits();
        updateStatus();
    }

    private void openFile() {
        if (!confirmSave()) return;
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fc.setDialogTitle("打开文件");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            textArea.setText(content);
            textArea.setCaretPosition(0);
            currentFile = file;
            modified    = false;
            setTitle("文本编辑器 — " + file.getName());
            undoMgr.discardAllEdits();
            updateStatus();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "打开失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        if (currentFile == null) { saveAs(); return; }
        writeFile(currentFile);
    }

    private void saveAs() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("另存为");
        fc.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        if (currentFile != null) fc.setSelectedFile(currentFile);
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        if (!file.getName().contains(".")) file = new File(file.getPath() + ".txt");
        if (file.exists()) {
            int r = JOptionPane.showConfirmDialog(this, "文件已存在，覆盖？", "确认", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
        }
        writeFile(file);
    }

    private void writeFile(File file) {
        try {
            Files.write(file.toPath(), textArea.getText().getBytes(StandardCharsets.UTF_8));
            currentFile = file;
            modified    = false;
            setTitle("文本编辑器 — " + file.getName());
            updateStatus();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "保存失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** @return true 表示可以继续（已保存或用户选择不保存） */
    private boolean confirmSave() {
        if (!modified) return true;
        int r = JOptionPane.showConfirmDialog(this,
                "文件已修改，是否保存？", "未保存的更改",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION)    { save(); return !modified; }
        if (r == JOptionPane.NO_OPTION)     return true;
        return false; // CANCEL
    }

    // ════════════════════════════════════════════════════════
    //  查找 / 替换
    // ════════════════════════════════════════════════════════

    private void openFindReplace() {
        JDialog dlg = new JDialog(this, "查找 / 替换", false);
        dlg.setResizable(false);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(new Color(248, 249, 252));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfFind    = new JTextField(24);
        JTextField tfReplace = new JTextField(24);
        JCheckBox  cbCase    = new JCheckBox("区分大小写");
        cbCase.setBackground(new Color(248, 249, 252));
        tfFind.setText(lastSearch);

        styleField(tfFind); styleField(tfReplace);

        g.gridx=0; g.gridy=0; dlg.add(new JLabel("查找："), g);
        g.gridx=1;             dlg.add(tfFind, g);
        g.gridx=0; g.gridy=1; dlg.add(new JLabel("替换："), g);
        g.gridx=1;             dlg.add(tfReplace, g);
        g.gridx=1; g.gridy=2; dlg.add(cbCase, g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        btns.setBackground(new Color(248, 249, 252));
        JButton btnFind    = makeSmallBtn("查找下一个", CLR_PRIMARY);
        JButton btnReplace = makeSmallBtn("替换",      new Color(80, 140, 80));
        JButton btnAll     = makeSmallBtn("全部替换",  new Color(80, 140, 80));
        JButton btnClose   = makeSmallBtn("关闭",      new Color(160, 60, 60));
        btns.add(btnFind); btns.add(btnReplace); btns.add(btnAll); btns.add(btnClose);

        g.gridx=0; g.gridy=3; g.gridwidth=2;
        dlg.add(btns, g);

        btnFind.addActionListener(e -> findNext(tfFind.getText(), cbCase.isSelected()));
        btnReplace.addActionListener(e -> replaceOne(tfFind.getText(), tfReplace.getText(), cbCase.isSelected()));
        btnAll.addActionListener(e -> replaceAll(tfFind.getText(), tfReplace.getText(), cbCase.isSelected()));
        btnClose.addActionListener(e -> { clearHighlights(); dlg.dispose(); });
        dlg.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { clearHighlights(); }
        });
        tfFind.addActionListener(e -> findNext(tfFind.getText(), cbCase.isSelected()));

        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void findNext(String target, boolean caseSensitive) {
        if (target.isEmpty()) return;
        lastSearch = target;
        String text   = textArea.getText();
        String needle = caseSensitive ? target : target.toLowerCase();
        String haystack = caseSensitive ? text : text.toLowerCase();

        int start = lastFindPos;
        int idx   = haystack.indexOf(needle, start);
        if (idx < 0) {           // 从头再找
            idx = haystack.indexOf(needle, 0);
            lastFindPos = 0;
        }
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "找不到：" + target);
            return;
        }
        textArea.setCaretPosition(idx);
        textArea.moveCaretPosition(idx + target.length());
        textArea.requestFocus();
        lastFindPos = idx + target.length();
        highlightAll(haystack, needle, idx);
    }

    private void replaceOne(String find, String replace, boolean caseSensitive) {
        String sel = textArea.getSelectedText();
        if (sel != null && (caseSensitive ? sel.equals(find) : sel.equalsIgnoreCase(find))) {
            textArea.replaceSelection(replace);
        }
        findNext(find, caseSensitive);
    }

    private void replaceAll(String find, String replace, boolean caseSensitive) {
        if (find.isEmpty()) return;
        String text = textArea.getText();
        // 先统计出现次数
        String needle   = caseSensitive ? find : find.toLowerCase();
        String haystack = caseSensitive ? text : text.toLowerCase();
        int count = 0, idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) { count++; idx += needle.length(); }

        String newText = caseSensitive
                ? text.replace(find, replace)
                : text.replaceAll("(?i)" + java.util.regex.Pattern.quote(find),
                                  java.util.regex.Matcher.quoteReplacement(replace));
        textArea.setText(newText);
        JOptionPane.showMessageDialog(this, "替换完成，共替换 " + count + " 处。", "完成", JOptionPane.INFORMATION_MESSAGE);
        clearHighlights();
    }

    private void highlightAll(String haystack, String needle, int currentIdx) {
        Highlighter h = textArea.getHighlighter();
        h.removeAllHighlights();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT);
        int idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) {
            try { h.addHighlight(idx, idx + needle.length(), painter); }
            catch (BadLocationException ignored) {}
            idx += needle.length();
        }
    }

    private void clearHighlights() {
        textArea.getHighlighter().removeAllHighlights();
        lastFindPos = 0;
    }

    // ════════════════════════════════════════════════════════
    //  字体 / 换行
    // ════════════════════════════════════════════════════════

    private void openFontChooser() {
        Font current = textArea.getFont();
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> cbFamily = new JComboBox<>(families);
        cbFamily.setSelectedItem(current.getFamily());
        cbFamily.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        JSpinner spSize = new JSpinner(new SpinnerNumberModel(current.getSize(), 8, 72, 1));
        spSize.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("字体：")); p.add(cbFamily);
        p.add(new JLabel("字号：")); p.add(spSize);

        int r = JOptionPane.showConfirmDialog(this, p, "字体设置", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            String family = (String) cbFamily.getSelectedItem();
            int    size   = (int) spSize.getValue();
            textArea.setFont(new Font(family, Font.PLAIN, size));
        }
    }

    private boolean wrapEnabled = true;
    private void toggleWrap() {
        wrapEnabled = !wrapEnabled;
        textArea.setLineWrap(wrapEnabled);
        textArea.setWrapStyleWord(wrapEnabled);
    }

    // ── 工具 ─────────────────────────────────────────────

    private JButton makeSmallBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleField(JTextField tf) {
        tf.setFont(new Font("Consolas", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    // ════════════════════════════════════════════════════════
    //  行号面板
    // ════════════════════════════════════════════════════════

    private static class LineNumberPanel extends JPanel {
        private final JTextArea area;

        LineNumberPanel(JTextArea area) {
            this.area = area;
            setPreferredSize(new Dimension(44, 0));
            setBackground(CLR_LINE_BG);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CLR_BORDER));
            area.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e)  { repaint(); }
                public void removeUpdate(DocumentEvent e)  { repaint(); }
                public void changedUpdate(DocumentEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Consolas", Font.PLAIN, area.getFont().getSize()));
            g.setColor(new Color(140, 148, 165));

            int lineHeight  = area.getFontMetrics(area.getFont()).getHeight();
            int startOffset = area.getInsets().top;
            int lineCount   = area.getLineCount();

            for (int i = 1; i <= lineCount; i++) {
                String num = String.valueOf(i);
                int x = getWidth() - g.getFontMetrics().stringWidth(num) - 6;
                int y = startOffset + (i - 1) * lineHeight + lineHeight - 4;
                g.drawString(num, x, y);
            }
        }
    }
}
