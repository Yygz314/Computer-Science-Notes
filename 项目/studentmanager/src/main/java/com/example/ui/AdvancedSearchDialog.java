package com.example.ui;

import com.example.bean.StudentBean;
import com.example.dao.StudentDao;

import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * 高级搜索对话框：支持多条件组合 + 模糊/精确切换
 */
public class AdvancedSearchDialog extends JDialog {

    private final JTextField   tfId       = UITheme.createField("");
    private final JTextField   tfName     = UITheme.createField("");
    private final JComboBox<String> cbGender = new JComboBox<>(new String[]{"不限", "男", "女"});
    private final JTextField   tfClazz    = UITheme.createField("");
    private final JTextField   tfScoreMin = UITheme.createField("");
    private final JTextField   tfScoreMax = UITheme.createField("");
    private final JCheckBox    cbFuzzy    = new JCheckBox("模糊匹配（姓名 / 班级）");

    public AdvancedSearchDialog(FrmMain owner) {
        super(owner, "高级搜索", true);
        setResizable(false);
        buildUI(owner);
        pack();
        setMinimumSize(new Dimension(480, 460));
        setLocationRelativeTo(owner);
    }

    private void buildUI(FrmMain owner) {
        // 标题
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        header.setBackground(UITheme.COLOR_PRIMARY);
        JLabel title = new JLabel("高级搜索");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        header.add(title);

        // 表单
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.COLOR_PANEL);
        form.setBorder(BorderFactory.createEmptyBorder(18, 30, 10, 30));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 6, 7, 6);

        cbGender.setFont(UITheme.FONT_NORMAL);
        cbGender.setPreferredSize(new Dimension(200, 36));
        cbFuzzy.setFont(UITheme.FONT_NORMAL);
        cbFuzzy.setBackground(UITheme.COLOR_PANEL);
        cbFuzzy.setSelected(true);

        addRow(form, g, 0, "学  号：", tfId);
        addRow(form, g, 1, "姓  名：", tfName);
        addRow(form, g, 2, "性  别：", cbGender);
        addRow(form, g, 3, "班  级：", tfClazz);

        // 分数区间
        g.gridx = 0; g.gridy = 4; g.gridwidth = 1; g.weightx = 0;
        form.add(UITheme.createLabel("分数区间："), g);
        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        scoreRow.setBackground(UITheme.COLOR_PANEL);
        tfScoreMin.setPreferredSize(new Dimension(90, 36));
        tfScoreMax.setPreferredSize(new Dimension(90, 36));
        scoreRow.add(tfScoreMin);
        scoreRow.add(UITheme.createLabel(" ~ "));
        scoreRow.add(tfScoreMax);
        g.gridx = 1; g.weightx = 1;
        form.add(scoreRow, g);

        // 模糊选项
        g.gridx = 1; g.gridy = 5;
        form.add(cbFuzzy, g);

        // 按钮
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        btns.setBackground(UITheme.COLOR_PANEL);
        JButton btnSearch = UITheme.createBtn("执行搜索");
        JButton btnReset  = UITheme.createBtn("重置条件");
        JButton btnCancel = UITheme.createDangerBtn("关  闭");
        btnReset.setBackground(new Color(100, 160, 100));
        btnReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnReset.setBackground(new Color(70, 130, 70)); }
            public void mouseExited (java.awt.event.MouseEvent e) { btnReset.setBackground(new Color(100, 160, 100)); }
        });
        btns.add(btnReset);
        btns.add(btnSearch);
        btns.add(btnCancel);

        btnSearch.addActionListener(e -> {
            String id     = tfId.getText().trim();
            String name   = tfName.getText().trim();
            String clazz  = tfClazz.getText().trim();
            String gender = cbGender.getSelectedIndex() == 0 ? "" : (String) cbGender.getSelectedItem();
            String sMin   = tfScoreMin.getText().trim();
            String sMax   = tfScoreMax.getText().trim();
            boolean fuzzy = cbFuzzy.isSelected();

            Integer scoreMin = null, scoreMax = null;
            try {
                if (!sMin.isEmpty()) scoreMin = Integer.parseInt(sMin);
                if (!sMax.isEmpty()) scoreMax = Integer.parseInt(sMax);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "分数须为整数！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (scoreMin != null && scoreMax != null && scoreMin > scoreMax) {
                JOptionPane.showMessageDialog(this, "最低分不能大于最高分！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<StudentBean> result = StudentDao.search(id, name, gender, clazz, scoreMin, scoreMax, fuzzy);
            owner.applySearchResult(result);
            JOptionPane.showMessageDialog(this,
                    "搜索完成，共找到 " + result.size() + " 条记录。", "结果", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        btnReset.addActionListener(e -> {
            tfId.setText(""); tfName.setText(""); tfClazz.setText("");
            tfScoreMin.setText(""); tfScoreMax.setText("");
            cbGender.setSelectedIndex(0); cbFuzzy.setSelected(true);
        });
        btnCancel.addActionListener(e -> dispose());

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        root.add(btns,   BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, Component field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0;
        p.add(UITheme.createLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }
}
