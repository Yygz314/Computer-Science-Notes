package com.webshopping.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(int id, String name, double price, int quantity) {
        int safeQty = quantity < 1 ? 1 : Math.min(quantity, 99);
        for (CartItem item : items) {
            if (item.getId() == id) {
                item.setQuantity(Math.min(item.getQuantity() + safeQty, 99));
                return;
            }
        }
        items.add(new CartItem(id, name, price, safeQty));
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}

