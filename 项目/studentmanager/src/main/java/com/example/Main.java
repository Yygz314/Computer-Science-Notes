package com.example;

import com.example.ui.StudentManagerLogin;
import com.example.ui.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UITheme.applyLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            StudentManagerLogin login = new StudentManagerLogin();
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            login.setVisible(true);
        });
    }
}
