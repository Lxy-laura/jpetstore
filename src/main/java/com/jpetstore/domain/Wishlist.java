package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Product> productMap = new ConcurrentHashMap<>();

    public void addProduct(Product product) {
        productMap.putIfAbsent(product.getProductid(), product);
    }

    public Product removeProduct(String productId) {
        return productMap.remove(productId);
    }

    public Collection<Product> getProducts() {
        return productMap.values();
    }

    public boolean contains(String productId) {
        return productMap.containsKey(productId);
    }

    public int getItemCount() {
        return productMap.size();
    }

    public void clear() {
        productMap.clear();
    }

    public boolean isEmpty() {
        return productMap.isEmpty();
    }
}
