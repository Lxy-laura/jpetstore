package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, CartItem> itemMap = new ConcurrentHashMap<>();

    public void addItem(Item item) {
        CartItem cartItem = itemMap.get(item.getItemid());
        if (cartItem == null) {
            cartItem = new CartItem(item);
            itemMap.put(item.getItemid(), cartItem);
        } else {
            cartItem.incrementQuantity();
        }
    }

    public Item removeItemById(String itemId) {
        CartItem cartItem = itemMap.remove(itemId);
        if (cartItem != null) {
            return cartItem.getItem();
        }
        return null;
    }

    public void incrementQuantity(String itemId) {
        CartItem cartItem = itemMap.get(itemId);
        if (cartItem != null) {
            cartItem.incrementQuantity();
        }
    }

    public void setQuantity(String itemId, int quantity) {
        CartItem cartItem = itemMap.get(itemId);
        if (cartItem != null) {
            if (quantity < 1) {
                itemMap.remove(itemId);
            } else {
                cartItem.setQuantity(quantity);
            }
        }
    }

    public Collection<CartItem> getCartItems() {
        return itemMap.values();
    }

    public int getNumberOfItems() {
        return itemMap.size();
    }

    public BigDecimal getSubTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem cartItem : itemMap.values()) {
            Item item = cartItem.getItem();
            if (item != null && item.getListprice() != null) {
                BigDecimal itemPrice = item.getListprice();
                BigDecimal quantity = new BigDecimal(cartItem.getQuantity());
                subTotal = subTotal.add(itemPrice.multiply(quantity));
            }
        }
        return subTotal;
    }

    public void clear() {
        itemMap.clear();
    }

    public boolean isEmpty() {
        return itemMap.isEmpty();
    }
}