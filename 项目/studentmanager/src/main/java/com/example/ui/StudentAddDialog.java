package com.example.ui;

import com.example.bean.StudentBean;
import com.example.dao.StudentDao;

import javax.swing.*;
import java.awt.*;

public class StudentAddDialog extends JDialog {

    private final JTextField     inputId     = UITheme.createField("");
    private final JTextField     inputName   = UITheme.createField("");
    private final JComboBox<String> inputGender = new JComboBox<>(new String[]{"", "男", "女"});
    private final JTextField     inputClazz  = UITheme.createField("");
    private final JTextField     inputScore  = UITheme.createField("");

    public StudentAddDialog(FrmMain owner) {
        super(owner, "添加学生", true);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_BG);
        buildUI(owner);
        pack();
        setMinimumSize(new Dimension(420, 360));
        setLocationRelativeTo(owner);
    }

    private void buildUI(FrmMain owner) {
        // 标题
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        header.setBackground(UITheme.COLOR_PRIMARY);
        JLabel title = new JLabel("添加学生信息");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        header.add(title);

        // 表单
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.COLOR_PANEL);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 6, 8, 6);

        inputGender.setFont(UITheme.FONT_NORMAL);
        inputGender.setPreferredSize(new Dimension(200, 36));

        addRow(form, g, 0, "学  号：", inputId);
        addRow(form, g, 1, "姓  名：", inputName);
        addRow(form, g, 2, "性  别：", inputGender);
        addRow(form, g, 3, "班  级：", inputClazz);
        addRow(form, g, 4, "分  数：", inputScore);

        // 按钮
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btns.setBackground(UITheme.COLOR_PANEL);
        JButton btnConfirm = UITheme.createBtn("确认添加");
        JButton btnCancel  = UITheme.createDangerBtn("取  消");
        btns.add(btnCancel);
        btns.add(btnConfirm);

        btnConfirm.addActionListener(e -> {
            // 验证学号
            String idStr = inputId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "学号不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                inputId.requestFocus();
                return;
            }
            long id;
            try {
                id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "学号须为正整数！", "提示", JOptionPane.WARNING_MESSAGE);
                inputId.requestFocus();
                return;
            }
            if (StudentDao.exists(id)) {
                JOptionPane.showMessageDialog(this,
                        "学号 " + id + " 已存在，请重新输入！", "提示", JOptionPane.WARNING_MESSAGE);
                inputId.selectAll();
                inputId.requestFocus();
                return;
            }

            // 验证姓名
            String name = inputName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 验证分数
            int score;
            try {
                score = Integer.parseInt(inputScore.getText().trim());
                if (score < 0 || score > 150) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "分数须为 0~150 之间的整数！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String clazz  = inputClazz.getText().trim();
            String gender  = (String) inputGender.getSelectedItem();

            StudentBean s = new StudentBean(id, name, score, gender, clazz);
            StudentDao.insert(s);
            owner.onAdded(s);
            dispose();
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

