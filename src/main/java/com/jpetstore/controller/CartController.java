package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Cart;
import com.jpetstore.domain.Item;
import com.jpetstore.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public Result<Cart> getCart(HttpSession session) {
        return Result.success(getOrCreateCart(session));
    }

    @PostMapping("/add")
    public Result<Cart> addToCart(@RequestParam String itemId, HttpSession session) {
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            return Result.notFound("商品不存在");
        }

        Cart cart = getOrCreateCart(session);
        cart.addItem(item);
        session.setAttribute("cart", cart);

        return Result.success("添加成功", cart);
    }

    @PostMapping("/remove")
    public Result<Cart> removeFromCart(@RequestParam String itemId, HttpSession session) {
        Cart cart = getOrCreateCart(session);
        Item removedItem = cart.removeItemById(itemId);

        if (removedItem != null) {
            session.setAttribute("cart", cart);
            return Result.success("移除成功", cart);
        }
        return Result.notFound("商品不在购物车中");
    }

    @PostMapping("/update")
    public Result<Cart> updateQuantity(@RequestParam String itemId, @RequestParam int quantity, HttpSession session) {
        Cart cart = getOrCreateCart(session);
        cart.setQuantity(itemId, quantity);
        session.setAttribute("cart", cart);

        return Result.success("更新成功", cart);
    }

    @PostMapping("/clear")
    public Result<Cart> clearCart(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        cart.clear();
        session.setAttribute("cart", cart);

        return Result.success("购物车已清空", cart);
    }

    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}