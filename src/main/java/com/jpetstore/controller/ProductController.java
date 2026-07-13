package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Item;
import com.jpetstore.domain.Product;
import com.jpetstore.service.ItemService;
import com.jpetstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ItemService itemService;

    @GetMapping
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }

    @GetMapping("/category/{category}")
    public Result<List<Product>> getProductsByCategory(@PathVariable String category) {
        return Result.success(productService.getProductsByCategory(category));
    }

    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        return Result.success(productService.searchProducts(keyword));
    }

    @GetMapping("/{productid}")
    public Result<Product> getProductById(@PathVariable String productid) {
        Product product = productService.getProductById(productid);
        if (product != null) {
            List<Item> items = itemService.getItemsByProductId(productid);
            product.setItems(items);
            return Result.success(product);
        }
        return Result.notFound("产品不存在");
    }

    @GetMapping("/{productid}/items")
    public Result<List<Item>> getProductItems(@PathVariable String productid) {
        return Result.success(itemService.getItemsByProductId(productid));
    }

    @PostMapping
    public Result<String> createProduct(@Valid @RequestBody Product product) {
        int result = productService.insertProduct(product);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(503, "创建失败");
    }

    @PutMapping("/{productid}")
    public Result<String> updateProduct(@PathVariable String productid, @Valid @RequestBody Product product) {
        product.setProductid(productid);
        int result = productService.updateProduct(product);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/{productid}")
    public Result<String> deleteProduct(@PathVariable String productid) {
        int result = productService.deleteProduct(productid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }
}