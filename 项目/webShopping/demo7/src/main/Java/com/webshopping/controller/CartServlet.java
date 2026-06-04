package com.webshopping.controller;

import com.webshopping.dao.CartDao;
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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CartDao cartDao = new CartDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/cart_view.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!WebSecurityUtil.verifyCsrfToken(req)) {
            redirectWithMsg(req, resp, "\u8bf7\u6c42\u6821\u9a8c\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5\u3002");
            return;
        }

        String action = trimToEmpty(req.getParameter("action"));
        String cartOwner = resolveCartOwner(req);

        try {
            if ("add".equals(action)) {
                handleAdd(req, cartOwner);
                redirectAfterAdd(req, resp, "\u5df2\u52a0\u5165\u8d2d\u7269\u8f66\u3002");
                return;
            }
            if ("remove".equals(action)) {
                int productId = parsePositiveInt(req.getParameter("productId"), "\u5546\u54c1ID\u65e0\u6548\u3002");
                cartDao.removeItem(cartOwner, productId);
                redirectWithMsg(req, resp, "\u5546\u54c1\u5df2\u4ece\u8d2d\u7269\u8f66\u79fb\u9664\u3002");
                return;
            }
            if ("update".equals(action)) {
                int productId = parsePositiveInt(req.getParameter("productId"), "\u5546\u54c1ID\u65e0\u6548\u3002");
                int quantity = parsePositiveInt(req.getParameter("quantity"), "\u6570\u91cf\u65e0\u6548\u3002");
                cartDao.updateQuantity(cartOwner, productId, quantity);
                redirectWithMsg(req, resp, "\u8d2d\u7269\u8f66\u6570\u91cf\u5df2\u66f4\u65b0\u3002");
                return;
            }
            if ("clear".equals(action)) {
                cartDao.clear(cartOwner);
                redirectWithMsg(req, resp, "\u8d2d\u7269\u8f66\u5df2\u6e05\u7a7a\u3002");
                return;
            }
            if ("checkout".equals(action)) {
                handleCheckout(req, resp, cartOwner);
                return;
            }
            redirectWithMsg(req, resp, "\u672a\u77e5\u64cd\u4f5c\u3002");
        } catch (Exception e) {
            redirectWithMsg(req, resp, "\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002");
        }
    }

    private void handleAdd(HttpServletRequest req, String cartOwner) throws Exception {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            idStr = req.getParameter("productId");
        }
        int productId = parsePositiveInt(idStr, "\u5546\u54c1ID\u65e0\u6548\u3002");
        int quantity = parsePositiveInt(req.getParameter("quantity"), "\u6570\u91cf\u65e0\u6548\u3002");
        cartDao.addItem(cartOwner, productId, quantity);
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp, String cartOwner) throws Exception {
        String user = (String) req.getSession().getAttribute("user");
        if (isBlank(user)) {
            String encoded = URLEncoder.encode("\u8bf7\u5148\u767b\u5f55\u540e\u518d\u7ed3\u7b97\u3002", StandardCharsets.UTF_8.name());
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=" + encoded);
            return;
        }
        String orderNo = orderDao.createOrderFromCart(cartOwner);
        if (isBlank(orderNo)) {
            redirectWithMsg(req, resp, "\u8d2d\u7269\u8f66\u4e3a\u7a7a\uff0c\u65e0\u6cd5\u7ed3\u7b97\u3002");
            return;
        }
        String encodedOrderNo = URLEncoder.encode(orderNo, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/order_success.jsp?orderNo=" + encodedOrderNo);
    }

    private int parsePositiveInt(String value, String errMsg) {
        try {
            int parsed = Integer.parseInt(trimToEmpty(value));
            if (parsed <= 0) {
                throw new IllegalArgumentException(errMsg);
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private String resolveCartOwner(HttpServletRequest req) {
        String user = (String) req.getSession().getAttribute("user");
        if (isBlank(user)) {
            return "guest_" + req.getSession().getId();
        }
        return user.trim();
    }

    private void redirectWithMsg(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/cart_view.jsp?msg=" + encoded);
    }

    private void redirectAfterAdd(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String returnUrl = normalizeReturnUrl(req.getParameter("returnUrl"));
        if (isBlank(returnUrl)) {
            redirectWithMsg(req, resp, msg);
            return;
        }
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        String separator = returnUrl.contains("?") ? "&" : "?";
        resp.sendRedirect(req.getContextPath() + "/" + returnUrl + separator + "msg=" + encoded);
    }

    private String normalizeReturnUrl(String raw) {
        String value = trimToEmpty(raw).replace('\\', '/');
        if (isBlank(value)) {
            return "";
        }
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (isBlank(value) || value.contains("..") || value.contains("://") || value.startsWith("//")) {
            return "";
        }
        String basePath = value;
        int idx = value.indexOf('?');
        if (idx >= 0) {
            basePath = value.substring(0, idx);
        }
        if (!basePath.endsWith(".jsp")) {
            return "";
        }
        return value;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
