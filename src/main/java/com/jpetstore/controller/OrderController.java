package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.*;
import com.jpetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> createOrder(@RequestBody Order order, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return Result.unauthorized("请先登录");
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return Result.badRequest("购物车为空");
        }

        // 从session中获取正确的userid，不依赖前端传值
        Order createdOrder = orderService.createOrder(user.getUserid(), cart, order);
        if (createdOrder != null) {
            cart.clear();
            session.setAttribute("cart", cart);
            return Result.success("订单创建成功", createdOrder);
        }
        return Result.error(500, "订单创建失败");
    }


    @GetMapping
    public Result<List<Order>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    @GetMapping("/{orderid}")
    public Result<Order> getOrderById(@PathVariable Integer orderid) {
        Order order = orderService.getOrderById(orderid);
        if (order != null) {
            return Result.success(order);
        }
        return Result.notFound("订单不存在");
    }

    @GetMapping("/my")
    public Result<List<Order>> getMyOrders(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        return Result.success(orderService.getOrdersByUserId(user.getUserid()));
    }

    @GetMapping("/user/{userid}")
    public Result<List<Order>> getOrdersByUserId(@PathVariable String userid) {
        return Result.success(orderService.getOrdersByUserId(userid));
    }

    @PutMapping("/{orderid}/status")
    public Result<String> updateOrderStatus(@PathVariable Integer orderid, @RequestParam String status) {
        boolean success = orderService.updateOrderStatus(orderid, status);
        if (success) {
            return Result.success("状态更新成功", "状态更新成功");
        }
        return Result.error(500, "状态更新失败");
    }

    @GetMapping("/{orderid}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Integer orderid) {
        return Result.success(orderService.getOrderItemsByOrderId(orderid));
    }

    @GetMapping("/{orderid}/status/history")
    public Result<List<OrderStatus>> getOrderStatusHistory(@PathVariable Integer orderid) {
        return Result.success(orderService.getOrderStatusByOrderId(orderid));
    }
}