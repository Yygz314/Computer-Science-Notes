package com.example.ui;

import com.example.bean.StudentBean;
import com.example.dao.StudentDao;

import javax.swing.*;
import java.awt.*;

public class StudentEditDialog extends JDialog {

    private final JTextField     inputName   = UITheme.createField("");
    private final JComboBox<String> inputGender = new JComboBox<>(new String[]{"", "男", "女"});
    private final JTextField     inputClazz  = UITheme.createField("");
    private final JTextField     inputScore  = UITheme.createField("");

    public StudentEditDialog(FrmMain owner, StudentBean student) {
        super(owner, "编辑学生", true);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_BG);

        // 预填数据
        inputName .setText(student.getName());
        inputClazz.setText(student.getClazz());
        inputScore.setText(String.valueOf(student.getScore()));
        inputGender.setSelectedItem(student.getGender());

        buildUI(owner, student);
        pack();
        setMinimumSize(new Dimension(420, 340));
        setLocationRelativeTo(owner);
    }

    private void buildUI(FrmMain owner, StudentBean student) {
        // 标题
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        header.setBackground(UITheme.COLOR_PRIMARY);
        JLabel title = new JLabel("编辑学生信息  —  学号 " + student.getID());
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

        // 学号只读显示框
        JTextField idDisplay = new JTextField(String.valueOf(student.getID()));
        idDisplay.setEditable(false);
        idDisplay.setFont(UITheme.FONT_NORMAL);
        idDisplay.setForeground(UITheme.COLOR_TEXT_HINT);
        idDisplay.setBackground(new Color(238, 240, 245));
        idDisplay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        idDisplay.setPreferredSize(new Dimension(200, 36));
        idDisplay.setToolTipText("学号不可修改");

        addRow(form, g, 0, "学  号：", idDisplay);
        addRow(form, g, 1, "姓  名：", inputName);
        addRow(form, g, 2, "性  别：", inputGender);
        addRow(form, g, 3, "班  级：", inputClazz);
        addRow(form, g, 4, "分  数：", inputScore);

        // 按钮
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btns.setBackground(UITheme.COLOR_PANEL);
        JButton btnConfirm = UITheme.createBtn("保存修改");
        JButton btnCancel  = UITheme.createDangerBtn("取  消");
        btns.add(btnCancel);
        btns.add(btnConfirm);

        btnConfirm.addActionListener(e -> {
            String name  = inputName.getText().trim();
            String clazz = inputClazz.getText().trim();
            String scoreStr = inputScore.getText().trim();
            String gender   = (String) inputGender.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int score;
            try {
                score = Integer.parseInt(scoreStr);
                if (score < 0 || score > 150) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "分数须为 0~150 之间的整数！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            student.setName(name);
            student.setScore(score);
            student.setGender(gender);
            student.setClazz(clazz);
            if (student.isUpdated()) {
                StudentDao.update(student);
                owner.onUpdated(student);
            }
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
