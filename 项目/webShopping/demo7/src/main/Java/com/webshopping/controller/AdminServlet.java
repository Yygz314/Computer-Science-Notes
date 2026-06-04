package com.webshopping.controller;

import com.webshopping.dao.MessageDao;
import com.webshopping.dao.UserDao;
import com.webshopping.model.CatalogBean;
import com.webshopping.model.ProductInfo;
import com.webshopping.model.Userinfo;
import com.webshopping.util.WebSecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@WebServlet("/admin")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 25 * 1024 * 1024
)
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();
    private final MessageDao messageDao = new MessageDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            redirectToLogin(req, resp, "\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650\u3002");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!isAdmin(req)) {
            redirectToLogin(req, resp, "\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650\u3002");
            return;
        }
        if (!WebSecurityUtil.verifyCsrfToken(req)) {
            redirectToAdmin(req, resp, "\u8bf7\u6c42\u6821\u9a8c\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u540e\u91cd\u8bd5\u3002");
            return;
        }

        String action = trimToEmpty(req.getParameter("action"));
        try {
            if ("setRole".equals(action)) {
                handleSetRole(req, resp);
                return;
            }
            if ("updateProduct".equals(action)) {
                handleUpdateProduct(req, resp);
                return;
            }
            if ("replyMessage".equals(action)) {
                handleReplyMessage(req, resp);
                return;
            }
            redirectToAdmin(req, resp, "\u672a\u77e5\u540e\u53f0\u64cd\u4f5c\u3002");
        } catch (Exception e) {
            String msg = trimToEmpty(e.getMessage());
            if (isBlank(msg)) {
                msg = "\u540e\u53f0\u64cd\u4f5c\u5931\u8d25\u3002";
            }
            redirectToAdmin(req, resp, msg);
        }
    }

    private void handleSetRole(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String username = trimToEmpty(req.getParameter("username"));
        String role = trimToEmpty(req.getParameter("role"));
        if (isBlank(username)) {
            redirectToAdmin(req, resp, "\u7528\u6237\u540d\u65e0\u6548\u3002");
            return;
        }
        if ("admin".equalsIgnoreCase(username)) {
            redirectToAdmin(req, resp, "\u9ed8\u8ba4\u7ba1\u7406\u5458\u89d2\u8272\u4e0d\u53ef\u4fee\u6539\u3002");
            return;
        }
        boolean ok = userDao.updateUserRole(username, role);
        redirectToAdmin(req, resp, ok ? "\u7528\u6237\u89d2\u8272\u66f4\u65b0\u6210\u529f\u3002" : "\u7528\u6237\u89d2\u8272\u66f4\u65b0\u5931\u8d25\u3002");
    }

    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = parsePositiveInt(req.getParameter("id"), "\u5546\u54c1ID\u65e0\u6548\u3002");

        ProductInfo product = new ProductInfo();
        product.setId(id);
        product.setName(trimToEmpty(req.getParameter("productName")));
        product.setPrice(parsePrice(req.getParameter("price"), "\u552e\u4ef7\u683c\u5f0f\u65e0\u6548\u3002"));
        product.setOriginalPrice(parsePrice(req.getParameter("originalPrice"), "\u539f\u4ef7\u683c\u5f0f\u65e0\u6548\u3002"));
        product.setSoldCount(parseNonNegativeInt(req.getParameter("soldCount"), "\u5df2\u552e\u6570\u91cf\u683c\u5f0f\u65e0\u6548\u3002"));
        product.setCategoryId(parsePositiveInt(req.getParameter("categoryId"), "\u5206\u7c7bID\u65e0\u6548\u3002"));

        String oldImagePath = cleanRelativePath(req.getParameter("imagePath"));
        String oldDetailImagePath = cleanRelativePath(req.getParameter("detailImagePath"));
        String imagePath = resolveUploadImage(req, "coverImage", oldImagePath, "cover");
        String detailImagePath = resolveUploadImage(req, "detailImage", oldDetailImagePath, "detail");
        product.setImagePath(imagePath);
        product.setDetailImagePath(detailImagePath);

        if (isBlank(product.getName()) || isBlank(product.getImagePath()) || isBlank(product.getDetailImagePath())) {
            redirectToAdmin(req, resp, "\u5546\u54c1\u540d\u79f0\u3001\u5c01\u9762\u56fe\u3001\u8be6\u60c5\u56fe\u4e0d\u80fd\u4e3a\u7a7a\u3002");
            return;
        }

        CatalogBean catalog = (CatalogBean) getServletContext().getAttribute("catalog");
        if (catalog == null) {
            catalog = new CatalogBean();
            getServletContext().setAttribute("catalog", catalog);
        }
        boolean ok = catalog.updateProduct(product);
        redirectToAdmin(req, resp, ok ? "\u5546\u54c1\u66f4\u65b0\u6210\u529f\u3002" : "\u5546\u54c1\u66f4\u65b0\u5931\u8d25\u3002");
    }

    private void handleReplyMessage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = parsePositiveInt(req.getParameter("id"), "\u7559\u8a00ID\u65e0\u6548\u3002");
        String reply = trimToEmpty(req.getParameter("reply"));
        String status = trimToEmpty(req.getParameter("status"));
        boolean ok = messageDao.updateMessageReply(id, reply, status);
        redirectToAdmin(req, resp, ok ? "\u7559\u8a00\u5904\u7406\u6210\u529f\u3002" : "\u7559\u8a00\u5904\u7406\u5931\u8d25\u3002");
    }

    private String resolveUploadImage(HttpServletRequest req, String partName, String oldPath, String prefix)
            throws IOException, ServletException {
        Part part = req.getPart(partName);
        if (part == null || part.getSize() <= 0) {
            return oldPath;
        }
        String submittedFileName = trimToEmpty(part.getSubmittedFileName());
        if (isBlank(submittedFileName)) {
            return oldPath;
        }

        String ext = resolveAllowedImageExt(submittedFileName, part.getContentType());
        if (isBlank(ext)) {
            throw new IllegalArgumentException("\u4ec5\u652f\u6301\u4e0a\u4f20 JPG/PNG/GIF/WEBP \u56fe\u7247\u3002");
        }

        String uploadRelativeDir = "Picture/uploads";
        String uploadDirPath = getServletContext().getRealPath("/" + uploadRelativeDir);
        if (isBlank(uploadDirPath)) {
            throw new IllegalStateException("\u670d\u52a1\u5668\u4e0a\u4f20\u76ee\u5f55\u4e0d\u53ef\u7528\u3002");
        }

        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("\u65e0\u6cd5\u521b\u5efa\u4e0a\u4f20\u76ee\u5f55\u3002");
        }

        String fileName = prefix + "_" + System.currentTimeMillis()
                + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
        File targetFile = new File(uploadDir, fileName);
        copyPartToFile(part, targetFile);
        return uploadRelativeDir + "/" + fileName;
    }

    private void copyPartToFile(Part part, File target) throws IOException {
        try (InputStream in = part.getInputStream();
             FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    private String resolveAllowedImageExt(String fileName, String contentType) {
        String lowerName = trimToEmpty(fileName).toLowerCase(Locale.ROOT);
        int dotIndex = lowerName.lastIndexOf('.');
        String ext = "";
        if (dotIndex >= 0 && dotIndex < lowerName.length() - 1) {
            ext = lowerName.substring(dotIndex);
        }
        if (".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext)
                || ".gif".equals(ext) || ".webp".equals(ext)) {
            return ext;
        }

        String lowerType = trimToEmpty(contentType).toLowerCase(Locale.ROOT);
        if ("image/jpeg".equals(lowerType)) {
            return ".jpg";
        }
        if ("image/png".equals(lowerType)) {
            return ".png";
        }
        if ("image/gif".equals(lowerType)) {
            return ".gif";
        }
        if ("image/webp".equals(lowerType)) {
            return ".webp";
        }
        return "";
    }

    private String cleanRelativePath(String rawPath) {
        String value = trimToEmpty(rawPath).replace('\\', '/');
        if (isBlank(value)) {
            return "";
        }
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (value.contains("..") || value.contains("://") || value.startsWith("//")) {
            return "";
        }
        return value;
    }

    private void redirectToAdmin(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/admin.jsp?msg=" + encoded);
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=" + encoded);
    }

    private boolean isAdmin(HttpServletRequest req) {
        Object userObj = req.getSession().getAttribute("loginUser");
        if (userObj instanceof Userinfo) {
            return ((Userinfo) userObj).isAdmin();
        }
        return false;
    }

    private int parsePositiveInt(String value, String errMsg) {
        try {
            int parsed = Integer.parseInt(trimToEmpty(value));
            if (parsed <= 0) {
                throw new IllegalArgumentException(errMsg);
            }
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private int parseNonNegativeInt(String value, String errMsg) {
        try {
            int parsed = Integer.parseInt(trimToEmpty(value));
            if (parsed < 0) {
                throw new IllegalArgumentException(errMsg);
            }
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private double parsePrice(String value, String errMsg) {
        try {
            double parsed = Double.parseDouble(trimToEmpty(value));
            if (parsed < 0) {
                throw new IllegalArgumentException(errMsg);
            }
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
