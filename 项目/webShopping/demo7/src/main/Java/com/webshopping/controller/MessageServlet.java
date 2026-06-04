package com.webshopping.controller;

import com.webshopping.dao.MessageDao;
import com.webshopping.model.Userinfo;
import com.webshopping.util.WebSecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MessageDao messageDao = new MessageDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/messages.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!WebSecurityUtil.verifyCsrfToken(req)) {
            redirectWithMsg(req, resp, "\u8bf7\u6c42\u6821\u9a8c\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5\u3002");
            return;
        }

        String action = trimToEmpty(req.getParameter("action"));
        if (!"add".equals(action)) {
            redirectWithMsg(req, resp, "\u672a\u77e5\u7559\u8a00\u64cd\u4f5c\u3002");
            return;
        }

        Userinfo loginUser = (Userinfo) req.getSession().getAttribute("loginUser");
        String author;
        if (loginUser != null && !isBlank(loginUser.getUsername())) {
            author = loginUser.getUsername().trim();
        } else {
            author = trimToEmpty(req.getParameter("author"));
            if (author.isEmpty()) {
                author = "\u6e38\u5ba2";
            }
        }
        String contact = trimToEmpty(req.getParameter("contact"));
        String content = trimToEmpty(req.getParameter("content"));

        if (content.isEmpty()) {
            redirectWithMsg(req, resp, "\u7559\u8a00\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a\u3002");
            return;
        }
        if (content.length() > 1000) {
            redirectWithMsg(req, resp, "\u7559\u8a00\u5185\u5bb9\u8fc7\u957f\u3002");
            return;
        }

        try {
            boolean ok = messageDao.addMessage(author, contact, content);
            redirectWithMsg(req, resp, ok ? "\u7559\u8a00\u63d0\u4ea4\u6210\u529f\u3002" : "\u7559\u8a00\u63d0\u4ea4\u5931\u8d25\u3002");
        } catch (Exception e) {
            redirectWithMsg(req, resp, "\u7559\u8a00\u63d0\u4ea4\u5931\u8d25\u3002");
        }
    }

    private void redirectWithMsg(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/messages.jsp?msg=" + encoded);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
