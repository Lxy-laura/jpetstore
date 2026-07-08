package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * 管理员控制器
 * 处理管理员专属的增删改查操作
 * 所有接口路径为/api/admin/**，受AdminInterceptor保护
 */
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

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/categories/{catid}")
    public Result<Category> getCategoryById(@PathVariable String catid) {
        Category category = categoryService.getCategoryById(catid);
        if (category != null) {
            return Result.success(category);
        }
        return Result.notFound("分类不存在");
    }

    /**
     * 创建分类
     */
    @PostMapping("/categories")
    public Result<String> createCategory(@Valid @RequestBody Category category) {
        int result = categoryService.insertCategory(category);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error("创建失败");
    }

    /**
     * 更新分类
     */
    @PutMapping("/categories/{catid}")
    public Result<String> updateCategory(@PathVariable String catid, @RequestBody Category category) {
        category.setCatid(catid);
        int result = categoryService.updateCategory(category);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{catid}")
    public Result<String> deleteCategory(@PathVariable String catid) {
        int result = categoryService.deleteCategory(catid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error("删除失败");
    }

    // ==================== 产品管理 ====================

    /**
     * 获取所有产品
     */
    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    /**
     * 根据ID获取产品
     */
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

    /**
     * 创建产品
     */
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
                return Result.error("图片上传失败: " + e.getMessage());
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
        return Result.error("创建失败");
    }

    /**
     * 更新产品
     */
    @PutMapping("/products/{productid}")
    public Result<String> updateProduct(@PathVariable String productid, @RequestBody Product product) {
        product.setProductid(productid);
        int result = productService.updateProduct(product);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/products/{productid}")
    public Result<String> deleteProduct(@PathVariable String productid) {
        int result = productService.deleteProduct(productid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error("删除失败");
    }

    // ==================== 商品项管理 ====================

    /**
     * 获取产品的所有商品项
     */
    @GetMapping("/products/{productid}/items")
    public Result<List<Item>> getProductItems(@PathVariable String productid) {
        List<Item> items = itemService.getItemsByProductId(productid);
        return Result.success(items);
    }

    /**
     * 创建商品项
     */
    @PostMapping("/items")
    public Result<String> createItem(@Valid @RequestBody Item item) {
        int result = itemService.insertItem(item);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error("创建失败");
    }

    /**
     * 更新商品项
     */
    @PutMapping("/items/{itemid}")
    public Result<String> updateItem(@PathVariable String itemid, @RequestBody Item item) {
        item.setItemid(itemid);
        int result = itemService.updateItem(item);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除商品项
     */
    @DeleteMapping("/items/{itemid}")
    public Result<String> deleteItem(@PathVariable String itemid) {
        int result = itemService.deleteItem(itemid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 更新库存
     */
    @PutMapping("/items/{itemid}/inventory")
    public Result<String> updateInventory(@PathVariable String itemid, @RequestParam int quantity) {
        int result = itemService.updateInventory(itemid, quantity);
        if (result > 0) {
            return Result.success("库存更新成功", "库存更新成功");
        }
        return Result.error("库存更新失败");
    }

    // ==================== 订单管理 ====================

    /**
     * 获取所有订单
     */
    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    /**
     * 根据ID获取订单
     */
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

    /**
     * 更新订单状态
     */
    @PutMapping("/orders/{orderid}/status")
    public Result<String> updateOrderStatus(@PathVariable Integer orderid, @RequestParam String status) {
        boolean success = orderService.updateOrderStatus(orderid, status);
        if (success) {
            return Result.success("状态更新成功", "状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public Result<List<Account>> getAllUsers() {
        List<Account> users = accountService.getAllUsers();
        return Result.success(users);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/users/{userid}")
    public Result<Account> getUserById(@PathVariable String userid) {
        Account account = accountService.getAccountByUsername(userid);
        if (account != null) {
            return Result.success(account);
        }
        return Result.notFound("用户不存在");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{userid}")
    public Result<String> updateUser(@PathVariable String userid, @RequestBody Account account) {
        account.setUserid(userid);
        boolean success = accountService.updateAccount(account);
        if (success) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userid}")
    public Result<String> deleteUser(@PathVariable String userid) {
        boolean success = accountService.deleteAccount(userid);
        if (success) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 更新用户角色
     */
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
        return Result.error("角色更新失败");
    }
}