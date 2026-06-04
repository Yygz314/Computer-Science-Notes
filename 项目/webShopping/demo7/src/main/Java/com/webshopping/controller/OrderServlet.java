package com.webshopping.controller;

import com.webshopping.dao.OrderDao;
import com.webshopping.util.WebSecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/orders.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String returnStatus = normalizeStatus(req.getParameter("returnStatus"));
        int returnPage = parsePositiveInt(req.getParameter("returnPage"), 1);
        if (!WebSecurityUtil.verifyCsrfToken(req)) {
            redirectWithMsg(req, resp, "\u8bf7\u6c42\u6821\u9a8c\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5\u3002", returnStatus, returnPage);
            return;
        }

        String user = (String) req.getSession().getAttribute("user");
        if (isBlank(user)) {
            redirectToLogin(req, resp, "\u8bf7\u5148\u767b\u5f55\u3002");
            return;
        }

        String action = trimToEmpty(req.getParameter("action"));
        String orderNo = trimToEmpty(req.getParameter("orderNo"));
        if (isBlank(orderNo)) {
            redirectWithMsg(req, resp, "\u8ba2\u5355\u53f7\u65e0\u6548\u3002", returnStatus, returnPage);
            return;
        }

        try {
            boolean ok;
            if ("pay".equals(action)) {
                ok = orderDao.markPaid(user.trim(), orderNo);
                if (ok) {
                    redirectWithMsg(req, resp, "\u8ba2\u5355\u652f\u4ed8\u6210\u529f\u3002", returnStatus, returnPage);
                } else {
                    String currentStatus = orderDao.getOrderStatusByOwnerAndOrderNo(user.trim(), orderNo);
                    redirectWithMsg(req, resp, buildStatusBlockedMessage("pay", currentStatus), returnStatus, returnPage);
                }
                return;
            }
            if ("cancel".equals(action)) {
                ok = orderDao.cancelOrder(user.trim(), orderNo);
                if (ok) {
                    redirectWithMsg(req, resp, "\u8ba2\u5355\u53d6\u6d88\u6210\u529f\u3002", returnStatus, returnPage);
                } else {
                    String currentStatus = orderDao.getOrderStatusByOwnerAndOrderNo(user.trim(), orderNo);
                    redirectWithMsg(req, resp, buildStatusBlockedMessage("cancel", currentStatus), returnStatus, returnPage);
                }
                return;
            }
            redirectWithMsg(req, resp, "\u672a\u77e5\u8ba2\u5355\u64cd\u4f5c\u3002", returnStatus, returnPage);
        } catch (Exception e) {
            redirectWithMsg(req, resp, "\u8ba2\u5355\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002", returnStatus, returnPage);
        }
    }

    private void redirectWithMsg(HttpServletRequest req, HttpServletResponse resp, String msg, String status, int page) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        StringBuilder url = new StringBuilder(req.getContextPath()).append("/orders.jsp?msg=").append(encoded);
        if (!isBlank(status)) {
            url.append("&status=").append(URLEncoder.encode(status, StandardCharsets.UTF_8.name()));
        }
        if (page > 1) {
            url.append("&page=").append(page);
        }
        resp.sendRedirect(url.toString());
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=" + encoded);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(trimToEmpty(value));
            return parsed > 0 ? parsed : defaultValue;
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    private String normalizeStatus(String status) {
        String value = trimToEmpty(status).toUpperCase();
        if ("PENDING".equals(value) || "PAID".equals(value) || "CANCELLED".equals(value)) {
            return value;
        }
        return "";
    }

    private String buildStatusBlockedMessage(String action, String currentStatus) {
        if (isBlank(currentStatus)) {
            return "\u8ba2\u5355\u4e0d\u5b58\u5728\u3002";
        }
        String normalized = currentStatus.trim().toUpperCase();
        if ("PAID".equals(normalized)) {
            if ("pay".equals(action)) {
                return "\u8ba2\u5355\u5df2\u652f\u4ed8\uff0c\u65e0\u9700\u91cd\u590d\u652f\u4ed8\u3002";
            }
            return "\u5df2\u652f\u4ed8\u8ba2\u5355\u4e0d\u80fd\u53d6\u6d88\u3002";
        }
        if ("CANCELLED".equals(normalized)) {
            if ("pay".equals(action)) {
                return "\u5df2\u53d6\u6d88\u8ba2\u5355\u4e0d\u80fd\u652f\u4ed8\u3002";
            }
            return "\u8ba2\u5355\u5df2\u53d6\u6d88\uff0c\u65e0\u9700\u91cd\u590d\u64cd\u4f5c\u3002";
        }
        if ("PENDING".equals(normalized)) {
            return "\u8ba2\u5355\u72b6\u6001\u5df2\u53d8\u5316\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5\u3002";
        }
        return "\u5f53\u524d\u8ba2\u5355\u72b6\u6001\u4e0d\u5141\u8bb8\u8be5\u64cd\u4f5c\u3002";
    }
}
