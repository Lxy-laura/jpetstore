package com.jpetstore.service;

import com.jpetstore.domain.Item;
import com.jpetstore.mapper.ItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商品项服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setItemid("ITEM001");
        testItem.setProductid("PROD001");
        testItem.setListprice(new BigDecimal("50.00"));
        testItem.setUnitcost(new BigDecimal("30.00"));
        testItem.setQty(100);
    }

    @Test
    void testGetItemsByProductId() {
        List<Item> items = Arrays.asList(testItem);
        when(itemMapper.getItemsByProductId("PROD001")).thenReturn(items);

        List<Item> result = itemService.getItemsByProductId("PROD001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ITEM001", result.get(0).getItemid());
        verify(itemMapper, times(1)).getItemsByProductId("PROD001");
    }

    @Test
    void testGetItemById() {
        when(itemMapper.getItemById("ITEM001")).thenReturn(testItem);

        Item result = itemService.getItemById("ITEM001");

        assertNotNull(result);
        assertEquals("ITEM001", result.getItemid());
        verify(itemMapper, times(1)).getItemById("ITEM001");
    }

    @Test
    void testGetItemByIdNotFound() {
        when(itemMapper.getItemById("NOTEXIST")).thenReturn(null);

        Item result = itemService.getItemById("NOTEXIST");

        assertNull(result);
        verify(itemMapper, times(1)).getItemById("NOTEXIST");
    }

    @Test
    void testGetAllItems() {
        List<Item> items = Arrays.asList(testItem);
        when(itemMapper.getAllItems()).thenReturn(items);

        List<Item> result = itemService.getAllItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemMapper, times(1)).getAllItems();
    }

    @Test
    void testUpdateInventory() {
        when(itemMapper.updateInventory("ITEM001", 10)).thenReturn(1);

        int result = itemService.updateInventory("ITEM001", 10);

        assertEquals(1, result);
        verify(itemMapper, times(1)).updateInventory("ITEM001", 10);
    }

    @Test
    void testInsertItem() {
        when(itemMapper.insertItem(any(Item.class))).thenReturn(1);

        int result = itemService.insertItem(testItem);

        assertEquals(1, result);
        verify(itemMapper, times(1)).insertItem(testItem);
    }

    @Test
    void testUpdateItem() {
        when(itemMapper.updateItem(any(Item.class))).thenReturn(1);

        int result = itemService.updateItem(testItem);

        assertEquals(1, result);
        verify(itemMapper, times(1)).updateItem(testItem);
    }

    @Test
    void testDeleteItem() {
        when(itemMapper.deleteItem("ITEM001")).thenReturn(1);

        int result = itemService.deleteItem("ITEM001");

        assertEquals(1, result);
        verify(itemMapper, times(1)).deleteItem("ITEM001");
    }
}