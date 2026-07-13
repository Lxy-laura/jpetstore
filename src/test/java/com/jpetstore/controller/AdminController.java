package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    // ==================== 分类管理 ====================

    @GetMapping("/categories")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/categories/{catid}")
    public Result<Category> getCategoryById(@PathVariable String catid) {
        Category category = categoryService.getCategoryById(catid);
        if (category != null) {
            return Result.success(category);
        }
        return Result.notFound("分类不存在");
    }

    @PostMapping("/categories")
    public Result<String> createCategory(@Valid @RequestBody Category category) {
        int result = categoryService.insertCategory(category);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(503, "创建失败");
    }

    @PutMapping("/categories/{catid}")
    public Result<String> updateCategory(@PathVariable String catid, @RequestBody Category category) {
        category.setCatid(catid);
        int result = categoryService.updateCategory(category);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/categories/{catid}")
    public Result<String> deleteCategory(@PathVariable String catid) {
        int result = categoryService.deleteCategory(catid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }

    // ==================== 产品管理 ====================

    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    @GetMapping("/products/{productid}")
    public Result<Product> getProductById(@PathVariable String productid) {
        Product product = productService.getProductById(productid);
        if (product != null) {
            List<Item> items = itemService.getItemsByProductId(productid);
            product.setItems(items);
            return Result.success(product);
        }
        return Result.notFound("产品不存在");
    }

    @PostMapping("/products")
    public Result<String> createProduct(@Valid @RequestBody Product product) {
        int result = productService.insertProduct(product);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(503, "创建失败");
    }

    @PutMapping("/products/{productid}")
    public Result<String> updateProduct(@PathVariable String productid, @RequestBody Product product) {
        product.setProductid(productid);
        int result = productService.updateProduct(product);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/products/{productid}")
    public Result<String> deleteProduct(@PathVariable String productid) {
        int result = productService.deleteProduct(productid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }

    // ==================== 商品项管理 ====================

    @GetMapping("/products/{productid}/items")
    public Result<List<Item>> getProductItems(@PathVariable String productid) {
        List<Item> items = itemService.getItemsByProductId(productid);
        return Result.success(items);
    }

    @PostMapping("/items")
    public Result<String> createItem(@Valid @RequestBody Item item) {
        int result = itemService.insertItem(item);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(503, "创建失败");
    }

    @PutMapping("/items/{itemid}")
    public Result<String> updateItem(@PathVariable String itemid, @RequestBody Item item) {
        item.setItemid(itemid);
        int result = itemService.updateItem(item);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/items/{itemid}")
    public Result<String> deleteItem(@PathVariable String itemid) {
        int result = itemService.deleteItem(itemid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }

    @PutMapping("/items/{itemid}/inventory")
    public Result<String> updateInventory(@PathVariable String itemid, @RequestParam int quantity) {
        int result = itemService.updateInventory(itemid, quantity);
        if (result > 0) {
            return Result.success("库存更新成功", "库存更新成功");
        }
        return Result.error(503, "库存更新失败");
    }

    // ==================== 订单管理 ====================

    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    @GetMapping("/orders/{orderid}")
    public Result<Order> getOrderById(@PathVariable Integer orderid) {
        Order order = orderService.getOrderById(orderid);
        if (order != null) {
            List<OrderItem> items = orderService.getOrderItemsByOrderId(orderid);
            order.setOrderItems(items);
            return Result.success(order);
        }
        return Result.notFound("订单不存在");
    }

    @PutMapping("/orders/{orderid}/status")
    public Result<String> updateOrderStatus(@PathVariable Integer orderid, @RequestParam String status) {
        boolean success = orderService.updateOrderStatus(orderid, status);
        if (success) {
            return Result.success("状态更新成功", "状态更新成功");
        }
        return Result.error(503, "状态更新失败");
    }

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public Result<List<Account>> getAllUsers() {
        List<Account> users = accountService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/users/{userid}")
    public Result<Account> getUserById(@PathVariable String userid) {
        Account account = accountService.getAccountByUsername(userid);
        if (account != null) {
            return Result.success(account);
        }
        return Result.notFound("用户不存在");
    }

    @PutMapping("/users/{userid}")
    public Result<String> updateUser(@PathVariable String userid, @RequestBody Account account) {
        account.setUserid(userid);
        boolean success = accountService.updateAccount(account);
        if (success) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/users/{userid}")
    public Result<String> deleteUser(@PathVariable String userid) {
        boolean success = accountService.deleteAccount(userid);
        if (success) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }

    @PutMapping("/users/{userid}/role")
    public Result<String> updateUserRole(@PathVariable String userid, @RequestParam String role) {
        Account account = accountService.getAccountByUsername(userid);
        if (account == null) {
            return Result.notFound("用户不存在");
        }
        account.setRole(role);
        boolean success = accountService.updateAccount(account);
        if (success) {
            return Result.success("角色更新成功", "角色更新成功");
        }
        return Result.error(503, "角色更新失败");
    }
}