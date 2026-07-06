package com.jpetstore.mapper;

import com.jpetstore.domain.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {

    List<Item> getItemsByProductId(@Param("productid") String productid);

    Item getItemById(@Param("itemid") String itemid);

    List<Item> getAllItems();

    int updateInventory(@Param("itemid") String itemid, @Param("quantity") int quantity);

    int insertItem(Item item);

    int updateItem(Item item);

    int deleteItem(@Param("itemid") String itemid);
}