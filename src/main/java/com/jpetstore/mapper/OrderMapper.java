package com.jpetstore.mapper;

import com.jpetstore.domain.Order;
import com.jpetstore.domain.OrderItem;
import com.jpetstore.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insertOrder(Order order);

    Order getOrderById(@Param("orderid") Integer orderid);

    List<Order> getOrdersByUserId(@Param("userid") String userid);

    List<Order> getAllOrders();

    int insertOrderItem(OrderItem orderItem);

    List<OrderItem> getOrderItemsByOrderId(@Param("orderid") Integer orderid);

    int insertOrderStatus(OrderStatus orderStatus);

    List<OrderStatus> getOrderStatusByOrderId(@Param("orderid") Integer orderid);

    int updateOrderStatus(@Param("orderid") Integer orderid, @Param("status") String status);
}