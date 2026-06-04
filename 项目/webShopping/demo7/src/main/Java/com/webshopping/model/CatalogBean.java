package com.webshopping.model;

import com.webshopping.dao.ProductDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class CatalogBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<ProductInfo> fallbackProducts = new ArrayList<>();
    private final Map<Integer, String> categories = new LinkedHashMap<>();
    private final ProductDao productDao = new ProductDao();

    public CatalogBean() {
        categories.put(1, "\u65e5\u5f0f\u5973\u6247");
        categories.put(2, "\u4eff\u53e4\u7537\u6247");
        categories.put(3, "\u97e9\u56fd\u6247");
        categories.put(4, "\u6a80\u9999\u6247");
        categories.put(5, "\u793c\u54c1\u5e7f\u544a\u6247");

        fallbackProducts.add(new ProductInfo(1, "\u6cb9\u6f06\u8fb9\u53cc\u8272\u9f99\u9aa8\u6247_\u7c89\u8272", 18, 28, 86, 1, "Picture/1.jpg", "Picture/source/d1.jpg"));
        fallbackProducts.add(new ProductInfo(2, "\u6cb9\u6f06\u8fb9\u53cc\u8272\u9f99\u9aa8\u6247_\u7ea2\u8272", 18, 28, 25, 1, "Picture/2.jpg", "Picture/source/d2.jpg"));
        fallbackProducts.add(new ProductInfo(3, "\u6cb9\u6f06\u8fb9\u53cc\u8272\u9f99\u9aa8\u6247_\u84dd\u8272", 17, 27, 39, 1, "Picture/3.jpg", "Picture/source/d3.jpg"));
        fallbackProducts.add(new ProductInfo(4, "\u6cb9\u6f06\u8fb9\u53cc\u8272\u9f99\u9aa8\u6247_\u7d2b\u8272", 16, 26, 19, 1, "Picture/4.jpg", "Picture/source/d4.jpg"));
        fallbackProducts.add(new ProductInfo(5, "\u77ed\u6897\u624b\u7ed8\u6298\u6247_\u6885\u82b1", 18, 28, 29, 4, "Picture/5.jpg", "Picture/source/d5.jpg"));
        fallbackProducts.add(new ProductInfo(6, "\u77ed\u6897\u624b\u7ed8\u6298\u6247_\u6843\u82b1", 18, 28, 86, 5, "Picture/6.jpg", "Picture/source/d6.jpg"));
        fallbackProducts.add(new ProductInfo(7, "\u6e05\u98ce\u6247_\u6d77\u68e0", 19, 29, 42, 2, "Picture/7.jpg", "Picture/source/d7.jpg"));
        fallbackProducts.add(new ProductInfo(8, "\u6e05\u98ce\u6247_\u9752\u8377", 20, 30, 31, 2, "Picture/8.jpg", "Picture/source/d8.jpg"));
        fallbackProducts.add(new ProductInfo(9, "\u6e05\u98ce\u6247_\u96e8\u71d5", 21, 31, 22, 3, "Picture/9.jpg", "Picture/source/d9.jpg"));
        fallbackProducts.add(new ProductInfo(10, "\u6e05\u98ce\u6247_\u661f\u6708", 22, 32, 18, 3, "Picture/10.jpg", "Picture/source/d10.jpg"));
        fallbackProducts.add(new ProductInfo(11, "\u6e05\u98ce\u6247_\u5170\u82b7", 23, 33, 27, 4, "Picture/11.jpg", "Picture/source/d11.jpg"));
        fallbackProducts.add(new ProductInfo(12, "\u6e05\u98ce\u6247_\u84dd\u96c0", 24, 34, 36, 5, "Picture/12.jpg", "Picture/source/d12.jpg"));
    }

    public List<ProductInfo> getProducts() {
        try {
            List<ProductInfo> list = productDao.findAll();
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (SQLException ignore) {
        }
        return Collections.unmodifiableList(fallbackProducts);
    }

    public int getTotalProducts() {
        try {
            return productDao.countAll();
        } catch (SQLException ignore) {
            return fallbackProducts.size();
        }
    }

    public int getTotalProductsByKeyword(String keyword) {
        try {
            return productDao.countByKeyword(keyword);
        } catch (SQLException ignore) {
            return filterFallbackByKeyword(keyword).size();
        }
    }

    public List<ProductInfo> getProductsByPage(int pageNo, int pageSize) {
        try {
            List<ProductInfo> list = productDao.findPage(pageNo, pageSize);
            if (list != null) {
                return list;
            }
        } catch (SQLException ignore) {
        }

        List<ProductInfo> all = getProducts();
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.max(1, pageSize);
        int from = (safePageNo - 1) * safePageSize;
        if (from >= all.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + safePageSize, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    public List<ProductInfo> getProductsByPageAndKeyword(int pageNo, int pageSize, String keyword) {
        try {
            List<ProductInfo> list = productDao.findPageByKeyword(keyword, pageNo, pageSize);
            if (list != null) {
                return list;
            }
        } catch (SQLException ignore) {
        }

        List<ProductInfo> filtered = filterFallbackByKeyword(keyword);
        int safePageNo = Math.max(1, pageNo);
        int safePageSize = Math.max(1, pageSize);
        int from = (safePageNo - 1) * safePageSize;
        if (from >= filtered.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + safePageSize, filtered.size());
        return new ArrayList<>(filtered.subList(from, to));
    }

    public List<ProductInfo> getLatestProducts() {
        try {
            List<ProductInfo> list = productDao.findLatest(6);
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (SQLException ignore) {
        }
        return getProducts();
    }

    public List<ProductInfo> getHotProducts() {
        try {
            List<ProductInfo> list = productDao.findHot(3);
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (SQLException ignore) {
        }
        List<ProductInfo> sorted = new ArrayList<>(getProducts());
        sorted.sort(Comparator.comparingInt(ProductInfo::getSoldCount).reversed());
        return sorted.subList(0, Math.min(3, sorted.size()));
    }

    public ProductInfo getProductById(int id) {
        try {
            ProductInfo product = productDao.findById(id);
            if (product != null) {
                return product;
            }
        } catch (SQLException ignore) {
        }
        for (ProductInfo product : fallbackProducts) {
            if (product.getId() == id) {
                return product;
            }
        }
        return fallbackProducts.isEmpty() ? null : fallbackProducts.get(0);
    }

    public void increaseProductClick(int id) {
        if (id <= 0) {
            return;
        }
        try {
            productDao.increaseClickCount(id);
        } catch (SQLException ignore) {
        }
    }

    public boolean updateProduct(ProductInfo product) {
        try {
            return productDao.updateProduct(product);
        } catch (SQLException ignore) {
            return false;
        }
    }

    public List<ProductInfo> getProductsByCategoryId(int categoryId) {
        try {
            List<ProductInfo> result = productDao.findByCategoryId(categoryId);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        } catch (SQLException ignore) {
        }

        List<ProductInfo> result = new ArrayList<>();
        for (ProductInfo product : fallbackProducts) {
            if (product.getCategoryId() == categoryId) {
                result.add(product);
            }
        }
        if (result.isEmpty()) {
            result.addAll(getProducts());
        }
        return result;
    }

    public Map<Integer, String> getCategories() {
        return Collections.unmodifiableMap(categories);
    }

    public String getCategoryName(int categoryId) {
        String name = categories.get(categoryId);
        return name == null ? "\u5168\u90e8\u5546\u54c1" : name;
    }

    private List<ProductInfo> filterFallbackByKeyword(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return new ArrayList<>(fallbackProducts);
        }
        List<ProductInfo> filtered = new ArrayList<>();
        for (ProductInfo product : fallbackProducts) {
            String name = product.getName();
            if (name != null && name.toLowerCase().contains(normalized)) {
                filtered.add(product);
            }
        }
        return filtered;
    }
}
