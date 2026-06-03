package com.example.ui;

import com.example.bean.UserBean;
import com.example.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class AdminChangePassword extends JDialog {

    private final JPasswordField inputOld     = UITheme.createPasswordField();
    private final JPasswordField inputNew     = UITheme.createPasswordField();
    private final JPasswordField inputConfirm = UITheme.createPasswordField();

    public AdminChangePassword(Window owner, UserBean user) {
        super(owner, "修改密码", ModalityType.APPLICATION_MODAL);
        setResizable(false);
        buildUI(user);
        pack();
        setMinimumSize(new Dimension(420, 340));
        setLocationRelativeTo(owner);
    }

    private void buildUI(UserBean user) {
        // 标题栏
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        header.setBackground(UITheme.COLOR_PRIMARY);
        JLabel title = new JLabel("修改管理员密码  —  " + user.getUsername());
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

        for (JPasswordField pf : new JPasswordField[]{inputOld, inputNew, inputConfirm}) {
            pf.setPreferredSize(new Dimension(240, 36));
        }

        addRow(form, g, 0, "旧密码：",  inputOld);
        addRow(form, g, 1, "新密码：",  inputNew);
        addRow(form, g, 2, "确认密码：", inputConfirm);

        // 提示
        g.gridx = 1; g.gridy = 3; g.gridwidth = 1;
        form.add(UITheme.createHintLabel("新密码长度至少 6 位"), g);

        // 按钮
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btns.setBackground(UITheme.COLOR_PANEL);
        JButton btnConfirm = UITheme.createBtn("确认修改");
        JButton btnCancel  = UITheme.createDangerBtn("取  消");
        btns.add(btnCancel);
        btns.add(btnConfirm);

        btnConfirm.addActionListener(e -> {
            String oldPwd     = new String(inputOld    .getPassword());
            String newPwd     = new String(inputNew    .getPassword());
            String confirmPwd = new String(inputConfirm.getPassword());

            if (!user.validatePassword(oldPwd)) {
                JOptionPane.showMessageDialog(this, "旧密码不正确！", "提示", JOptionPane.WARNING_MESSAGE);
                inputOld.setText("");
                inputOld.requestFocus();
                return;
            }
            if (newPwd.length() < 6) {
                JOptionPane.showMessageDialog(this, "新密码长度至少 6 位！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                JOptionPane.showMessageDialog(this, "两次输入的新密码不一致！", "提示", JOptionPane.WARNING_MESSAGE);
                inputConfirm.setText("");
                inputConfirm.requestFocus();
                return;
            }
            user.setPassword(newPwd);
            UserDao.update(user);
            JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
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
