package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PageController {

    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;
    @Autowired private ItemService itemService;
    @Autowired private AccountService accountService;
    @Autowired private OrderService orderService;
    @Autowired private RecommendationService recommendationService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("recommendations", recommendationService.getRecommendations(session, 8));
        model.addAttribute("recentViews", recommendationService.getRecentViews(session));
        return "index";
    }

    @GetMapping("/category/{catid}")
    public String category(@PathVariable String catid, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(catid));
        model.addAttribute("products", productService.getProductsByCategory(catid));
        return "category";
    }

    @GetMapping("/product/{productid}")
    public String product(@PathVariable String productid, Model model, HttpSession session) {
        Product product = productService.getProductById(productid);
        if (product != null) {
            product.setItems(itemService.getItemsByProductId(productid));
            recommendationService.recordView(session, product);
            model.addAttribute("relatedProducts", recommendationService.getRelatedProducts(productid, 4));
        }
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("/login") public String loginPage() { return "login"; }
    @GetMapping("/register") public String registerPage() { return "register"; }

    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        model.addAttribute("cart", cart == null ? new Cart() : cart);
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        return "checkout";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("orders", orderService.getOrdersByUserId(user.getUserid()));
        return "orders";
    }

    @GetMapping("/order/{orderid}")
    public String orderDetail(@PathVariable Integer orderid, Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Order order = orderService.getOrderById(orderid);
        if (order != null) {
            order.setOrderItems(orderService.getOrderItemsByOrderId(orderid));
        }
        model.addAttribute("order", order);
        return "order-detail";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("keyword", keyword);
        return "search";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!user.isAdmin()) return "redirect:/";

        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("productCount", productService.getAllProducts().size());
        model.addAttribute("orderCount", orderService.getAllOrders().size());
        model.addAttribute("userCount", accountService.getAllUsers().size());

        return "admin";
    }

    /**
     * 退出登录（页面路由）
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

