package com.webshopping.controller;

import com.webshopping.dao.UserDao;
import com.webshopping.model.Userinfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String username = trimToEmpty(req.getParameter("txtName"));
        String pwd = trimToEmpty(req.getParameter("pwd"));

        if (username.isEmpty() || pwd.isEmpty()) {
            redirectWithMsg(req, resp, "\u7528\u6237\u540d\u548c\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a\u3002");
            return;
        }

        Userinfo loginUser;
        try {
            loginUser = userDao.login(username, pwd);
        } catch (SQLException e) {
            redirectWithMsg(req, resp, "\u6570\u636e\u5e93\u8fde\u63a5\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002");
            return;
        }

        if (loginUser == null) {
            redirectWithMsg(req, resp, "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef\u3002");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("user", loginUser.getUsername());
        session.setAttribute("loginUser", loginUser);

        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }

    private void redirectWithMsg(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=" + encoded);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
