package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;
    @Autowired private ItemService itemService;
    @Autowired private OrderService orderService;
    @Autowired private AccountService accountService;

    // ==================== 分类管理 ====================
    @GetMapping("/categories")
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/categories/{catid}")
    public Result<Category> getCategoryById(@PathVariable String catid) {
        Category category = categoryService.getCategoryById(catid);
        return category != null ? Result.success(category) : Result.notFound("分类不存在");
    }

    @PostMapping("/categories")
    public Result<String> createCategory(@Valid @RequestBody Category category) {
        return categoryService.insertCategory(category) > 0
                ? Result.success("创建成功", "创建成功")
                : Result.error(500, "创建失败");
    }

    @PutMapping("/categories/{catid}")
    public Result<String> updateCategory(@PathVariable String catid, @RequestBody Category category) {
        category.setCatid(catid);
        return categoryService.updateCategory(category) > 0
                ? Result.success("更新成功", "更新成功")
                : Result.error(500, "更新失败");
    }

    @DeleteMapping("/categories/{catid}")
    public Result<String> deleteCategory(@PathVariable String catid) {
        return categoryService.deleteCategory(catid) > 0
                ? Result.success("删除成功", "删除成功")
                : Result.error(500, "删除失败");
    }

    // ==================== 产品管理 ====================
    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }

    @GetMapping("/products/{productid}")
    public Result<Product> getProductById(@PathVariable String productid) {
        Product product = productService.getProductById(productid);
        if (product != null) {
            product.setItems(itemService.getItemsByProductId(productid));
            return Result.success(product);
        }
        return Result.notFound("产品不存在");
    }

    @PostMapping("/products")
    public Result<String> createProduct(
            @RequestParam String productid,
            @RequestParam String category,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image) {

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String newFilename = UUID.randomUUID().toString() + extension;
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = "/uploads/" + newFilename;
            } catch (IOException e) {
                return Result.error(500, "图片上传失败: " + e.getMessage());
            }
        }

        Product product = new Product();
        product.setProductid(productid);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setImage(imagePath);

        int result = productService.insertProduct(product);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(500, "创建失败");
    }
    /**
     * 更新产品
     */
    @PutMapping("/products/{productid}")
    public Result<String> updateProduct(
            @PathVariable String productid,
            @RequestParam String category,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) MultipartFile image) {

        Product existing = productService.getProductById(productid);
        if (existing == null) {
            return Result.notFound("产品不存在");
        }

        String imagePath = existing.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String newFilename = UUID.randomUUID().toString() + extension;
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = "/uploads/" + newFilename;
            } catch (IOException e) {
                return Result.error(500, "图片上传失败: " + e.getMessage());
            }
        }

        Product product = new Product();
        product.setProductid(productid);
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setImage(imagePath);
        product.setPrice(price != null ? price : existing.getPrice());
        product.setStatus(status != null && !status.isEmpty() ? status : existing.getStatus());

        int result = productService.updateProduct(product);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @DeleteMapping("/products/{productid}")
    public Result<String> deleteProduct(@PathVariable String productid) {
        return productService.deleteProduct(productid) > 0
                ? Result.success("删除成功", "删除成功")
                : Result.error(500, "删除失败");
    }

    // ==================== 商品项管理 ====================
    @GetMapping("/products/{productid}/items")
    public Result<List<Item>> getProductItems(@PathVariable String productid) {
        return Result.success(itemService.getItemsByProductId(productid));
    }

    @PostMapping("/items")
    public Result<String> createItem(@Valid @RequestBody Item item) {
        return itemService.insertItem(item) > 0
                ? Result.success("创建成功", "创建成功")
                : Result.error(500, "创建失败");
    }

    @PutMapping("/items/{itemid}")
    public Result<String> updateItem(@PathVariable String itemid, @RequestBody Item item) {
        item.setItemid(itemid);
        return itemService.updateItem(item) > 0
                ? Result.success("更新成功", "更新成功")
                : Result.error(500, "更新失败");
    }

    @DeleteMapping("/items/{itemid}")
    public Result<String> deleteItem(@PathVariable String itemid) {
        return itemService.deleteItem(itemid) > 0
                ? Result.success("删除成功", "删除成功")
                : Result.error(500, "删除失败");
    }

    @PutMapping("/items/{itemid}/inventory")
    public Result<String> updateInventory(@PathVariable String itemid, @RequestParam int quantity) {
        return itemService.updateInventory(itemid, quantity) > 0
                ? Result.success("库存更新成功", "库存更新成功")
                : Result.error(500, "库存更新失败");
    }
    /**
     * 上下架产品
     */

    @PutMapping("/products/{productid}/status")
    public Result<String> updateProductStatus(
            @PathVariable String productid,
            @RequestParam String status) {
        if (!"ON_SALE".equals(status) && !"OFF_SALE".equals(status)) {
            return Result.error(500, "状态值无效");
        }
        int result = productService.updateProductStatus(productid, status);
        if (result > 0) {
            return Result.success("状态更新成功", "状态更新成功");
        }
        return Result.error(500, "状态更新失败");
    }

    // ==================== 订单管理 ====================
    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    @GetMapping("/orders/{orderid}")
    public Result<Order> getOrderById(@PathVariable Integer orderid) {
        Order order = orderService.getOrderById(orderid);
        if (order != null) {
            order.setOrderItems(orderService.getOrderItemsByOrderId(orderid));
            return Result.success(order);
        }
        return Result.notFound("订单不存在");
    }

    @PutMapping("/orders/{orderid}/status")
    public Result<String> updateOrderStatus(@PathVariable Integer orderid, @RequestParam String status) {
        return orderService.updateOrderStatus(orderid, status)
                ? Result.success("状态更新成功", "状态更新成功")
                : Result.error(500, "状态更新失败");
    }

    // ==================== 用户管理 ====================
    @GetMapping("/users")
    public Result<List<Account>> getAllUsers() {
        return Result.success(accountService.getAllUsers());
    }

    @GetMapping("/users/{userid}")
    public Result<Account> getUserById(@PathVariable String userid) {
        Account account = accountService.getAccountByUsername(userid);
        return account != null ? Result.success(account) : Result.notFound("用户不存在");
    }

    @PutMapping("/users/{userid}")
    public Result<String> updateUser(@PathVariable String userid, @RequestBody Account account) {
        account.setUserid(userid);
        return accountService.updateAccount(account)
                ? Result.success("更新成功", "更新成功")
                : Result.error(500, "更新失败");
    }

    @DeleteMapping("/users/{userid}")
    public Result<String> deleteUser(@PathVariable String userid) {
        return accountService.deleteAccount(userid)
                ? Result.success("删除成功", "删除成功")
                : Result.error(500, "删除失败");
    }

    @PutMapping("/users/{userid}/role")
    public Result<String> updateUserRole(@PathVariable String userid, @RequestParam String role) {
        Account account = accountService.getAccountByUsername(userid);
        if (account == null) return Result.notFound("用户不存在");
        account.setRole(role);
        return accountService.updateAccount(account)
                ? Result.success("角色更新成功", "角色更新成功")
                : Result.error(500, "角色更新失败");
    }
}