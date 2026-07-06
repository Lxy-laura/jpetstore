package com.jpetstore.service;

import com.jpetstore.domain.*;
import com.jpetstore.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ItemService itemService;

    @Transactional
    public Order createOrder(String userid, Cart cart, Order order) {
        try {
            order.setUserid(userid);
            order.setOrderdate(new Date());
            order.setTotalprice(cart.getSubTotal());

            orderMapper.insertOrder(order);

            int lineNum = 1;
            for (CartItem cartItem : cart.getCartItems()) {
                Item item = cartItem.getItem();
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderid(order.getOrderid());
                orderItem.setLinenum(lineNum++);
                orderItem.setItemid(item.getItemid());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitprice(item.getListprice());

                orderMapper.insertOrderItem(orderItem);
                itemService.updateInventory(item.getItemid(), cartItem.getQuantity());
            }

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOrderid(order.getOrderid());
            orderStatus.setLinenum(1);
            orderStatus.setTimestamp(new Date());
            orderStatus.setStatus("P");

            orderMapper.insertOrderStatus(orderStatus);

            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Order getOrderById(Integer orderid) {
        return orderMapper.getOrderById(orderid);
    }

    public List<Order> getOrdersByUserId(String userid) {
        return orderMapper.getOrdersByUserId(userid);
    }

    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    @Transactional
    public boolean updateOrderStatus(Integer orderid, String status) {
        try {
            orderMapper.updateOrderStatus(orderid, status);

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOrderid(orderid);
            orderStatus.setLinenum(1);
            orderStatus.setTimestamp(new Date());
            orderStatus.setStatus(status);
            orderMapper.insertOrderStatus(orderStatus);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(Integer orderid) {
        return orderMapper.getOrderItemsByOrderId(orderid);
    }

    public List<OrderStatus> getOrderStatusByOrderId(Integer orderid) {
        return orderMapper.getOrderStatusByOrderId(orderid);
    }
}