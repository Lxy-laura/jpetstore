package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Item;
import com.jpetstore.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public Result<List<Item>> getAllItems() {
        return Result.success(itemService.getAllItems());
    }

    @GetMapping("/{itemid}")
    public Result<Item> getItemById(@PathVariable String itemid) {
        Item item = itemService.getItemById(itemid);
        if (item != null) {
            return Result.success(item);
        }
        return Result.notFound("商品项不存在");
    }

    @PostMapping
    public Result<String> createItem(@Valid @RequestBody Item item) {
        int result = itemService.insertItem(item);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(500, "创建失败");
    }

    @PutMapping("/{itemid}")
    public Result<String> updateItem(@PathVariable String itemid, @Valid @RequestBody Item item) {
        item.setItemid(itemid);
        int result = itemService.updateItem(item);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(500, "更新失败");
    }

    @DeleteMapping("/{itemid}")
    public Result<String> deleteItem(@PathVariable String itemid) {
        int result = itemService.deleteItem(itemid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(500, "删除失败");
    }
}