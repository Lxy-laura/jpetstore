package com.jpetstore.service;

import com.jpetstore.domain.Item;
import com.jpetstore.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;

    public List<Item> getItemsByProductId(String productid) {
        return itemMapper.getItemsByProductId(productid);
    }

    public Item getItemById(String itemid) {
        return itemMapper.getItemById(itemid);
    }

    public List<Item> getAllItems() {
        return itemMapper.getAllItems();
    }

    @Transactional
    public int updateInventory(String itemid, int quantity) {
        return itemMapper.updateInventory(itemid, quantity);
    }

    @Transactional
    public int insertItem(Item item) {
        return itemMapper.insertItem(item);
    }

    @Transactional
    public int updateItem(Item item) {
        return itemMapper.updateItem(item);
    }

    @Transactional
    public int deleteItem(String itemid) {
        return itemMapper.deleteItem(itemid);
    }
}