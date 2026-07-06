package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * 页面路由控制器
 * 负责将请求转发到对应的Thymeleaf模板页面
 */
@Controller
public class PageController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.getAllProducts();
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "index";
    }

    /**
     * 分类商品列表页
     */
    @GetMapping("/category/{catid}")
    public String category(@PathVariable String catid, Model model) {
        Category category = categoryService.getCategoryById(catid);
        List<Product> products = productService.getProductsByCategory(catid);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "category";
    }

    /**
     * 商品详情页
     */
    @GetMapping("/product/{productid}")
    public String product(@PathVariable String productid, Model model) {
        Product product = productService.getProductById(productid);
        List<Item> items = itemService.getItemsByProductId(productid);
        if (product != null) {
            product.setItems(items);
        }
        model.addAttribute("product", product);
        return "product";
    }

    /**
     * 登录页
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 注册页
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 购物车页
     */
    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        model.addAttribute("cart", cart);
        return "cart";
    }

    /**
     * 结算页
     */
    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        return "checkout";
    }

    /**
     * 订单列表页
     */
    @GetMapping("/orders")
    public String ordersPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderService.getOrdersByUserId(user.getUserid());
        model.addAttribute("orders", orders);
        return "orders";
    }

    /**
     * 订单详情页
     */
    @GetMapping("/order/{orderid}")
    public String orderDetail(@PathVariable Integer orderid, Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Order order = orderService.getOrderById(orderid);
        if (order != null) {
            List<OrderItem> items = orderService.getOrderItemsByOrderId(orderid);
            order.setOrderItems(items);
        }
        model.addAttribute("order", order);
        return "order-detail";
    }

    /**
     * 个人中心页
     */
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * 搜索页
     */
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "search";
    }

    /**
     * 管理员后台页
     */
    @GetMapping("/admin")
    public String adminPage(Model model, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.isAdmin()) {
            return "redirect:/";
        }

        // 获取统计数据
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        List<Account> users = accountService.getAllUsers();

        model.addAttribute("categoryCount", categories.size());
        model.addAttribute("productCount", products.size());
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("userCount", users.size());

        return "admin";
    }
}