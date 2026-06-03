package com.example.ui;

import com.example.bean.UserBean;
import com.example.dao.UserDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class StudentManagerLogin extends JFrame {

    private final JTextField     txtAccount  = UITheme.createField("");
    private final JPasswordField txtPassword = UITheme.createPasswordField();
    private final JButton        btnLogin    = UITheme.createBtn("登 录");
    private final JButton        btnExit     = UITheme.createDangerBtn("退 出");

    public StudentManagerLogin() {
        setTitle("学生成绩管理系统 · 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_BG);

        buildUI();
        bindEvents();

        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        // ── 顶部标题栏 ──
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(460, 80));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("学生成绩管理系统");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // ── 表单卡片 ──
        JPanel card = new JPanel();
        card.setBackground(UITheme.COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 4, 8, 4);

        // 账号行
        g.gridx = 0; g.gridy = 0; g.gridwidth = 1;
        card.add(UITheme.createLabel("账  号："), g);
        g.gridx = 1; g.gridwidth = 2;
        txtAccount.setPreferredSize(new Dimension(240, 36));
        card.add(txtAccount, g);

        // 密码行
        g.gridx = 0; g.gridy = 1; g.gridwidth = 1;
        card.add(UITheme.createLabel("密  码："), g);
        g.gridx = 1; g.gridwidth = 2;
        txtPassword.setPreferredSize(new Dimension(240, 36));
        card.add(txtPassword, g);

        // 按钮行
        g.gridy = 2; g.gridx = 1; g.gridwidth = 1;
        g.insets = new Insets(20, 4, 4, 8);
        btnLogin.setPreferredSize(new Dimension(110, 38));
        card.add(btnLogin, g);

        g.gridx = 2;
        g.insets = new Insets(20, 8, 4, 4);
        btnExit.setPreferredSize(new Dimension(110, 38));
        card.add(btnExit, g);

        // ── 底部版权 ──
        JLabel footer = UITheme.createHintLabel("© 2026  学生成绩管理系统");
        footer.setHorizontalAlignment(SwingConstants.CENTER);

        // ── 总体布局 ──
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.COLOR_BG);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.COLOR_BG);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        center.add(card);

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(center,      BorderLayout.CENTER);
        root.add(footer,      BorderLayout.SOUTH);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        setContentPane(root);
    }

    private void bindEvents() {
        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        // 回车触发登录
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtAccount.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void doLogin() {
        String account  = txtAccount.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (account.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "账号和密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<UserBean> user = UserDao.login(account, password);
        if (user.isPresent()) {
            dispose();
            FrmMain main = new FrmMain(user.get());
            main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            main.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "账号不存在或密码错误，请重试。", "登录失败", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
