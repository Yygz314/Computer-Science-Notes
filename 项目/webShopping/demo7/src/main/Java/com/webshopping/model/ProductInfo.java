package com.webshopping.model;

import java.io.Serializable;

public class ProductInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double price;
    private double originalPrice;
    private int soldCount;
    private int clickCount;
    private int categoryId;
    private String imagePath;
    private String detailImagePath;

    public ProductInfo() {
    }

    public ProductInfo(int id, String name, double price, double originalPrice, int soldCount,
                       int categoryId, String imagePath, String detailImagePath) {
        this(id, name, price, originalPrice, soldCount, 0, categoryId, imagePath, detailImagePath);
    }

    public ProductInfo(int id, String name, double price, double originalPrice, int soldCount, int clickCount,
                       int categoryId, String imagePath, String detailImagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.soldCount = soldCount;
        this.clickCount = clickCount;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
        this.detailImagePath = detailImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDetailImagePath() {
        return detailImagePath;
    }

    public void setDetailImagePath(String detailImagePath) {
        this.detailImagePath = detailImagePath;
    }
}

