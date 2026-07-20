package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Product;
import com.jpetstore.domain.Wishlist;
import com.jpetstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<Wishlist> getWishlist(HttpSession session) {
        return Result.success(getOrCreateWishlist(session));
    }

    @PostMapping("/add/{productId}")
    public Result<Wishlist> addToWishlist(@PathVariable String productId, HttpSession session) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return Result.notFound("商品不存在");
        }
        Wishlist wishlist = getOrCreateWishlist(session);
        wishlist.addProduct(product);
        session.setAttribute("wishlist", wishlist);
        return Result.success("已添加到心愿单", wishlist);
    }

    @PostMapping("/remove/{productId}")
    public Result<Wishlist> removeFromWishlist(@PathVariable String productId, HttpSession session) {
        Wishlist wishlist = getOrCreateWishlist(session);
        wishlist.removeProduct(productId);
        session.setAttribute("wishlist", wishlist);
        return Result.success("已从心愿单移除", wishlist);
    }

    @PostMapping("/clear")
    public Result<Wishlist> clearWishlist(HttpSession session) {
        Wishlist wishlist = getOrCreateWishlist(session);
        wishlist.clear();
        session.setAttribute("wishlist", wishlist);
        return Result.success("心愿单已清空", wishlist);
    }

    private Wishlist getOrCreateWishlist(HttpSession session) {
        Wishlist wishlist = (Wishlist) session.getAttribute("wishlist");
        if (wishlist == null) {
            wishlist = new Wishlist();
            session.setAttribute("wishlist", wishlist);
        }
        return wishlist;
    }
}
