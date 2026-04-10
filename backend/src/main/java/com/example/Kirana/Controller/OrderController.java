package com.example.Kirana.Controller;

import com.example.Kirana.DTOs.Requests.CreateOrderRequest;
import com.example.Kirana.DTOs.Requests.UpdateOrderStatusRequest;
import com.example.Kirana.DTOs.Response.OrderResponse;
import com.example.Kirana.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer creates order
    @PostMapping("/")
    public ResponseEntity<OrderResponse> createOrder(
            Authentication auth,
            @RequestBody CreateOrderRequest request) {
        String customerId = auth.getPrincipal().toString();
        return ResponseEntity.ok(orderService.createOrder(customerId, request));
    }

    // Customer views their own orders
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication auth) {
        String customerId = auth.getPrincipal().toString();
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }

    // Shop owner views all orders for their shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<OrderResponse>> getShopOrders(@PathVariable String shopId) {
        return ResponseEntity.ok(orderService.getShopOrders(shopId));
    }

    // Shop owner filters orders by status
    @GetMapping("/shop/{shopId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getShopOrdersByStatus(
            @PathVariable String shopId,
            @PathVariable String status) {
        return ResponseEntity.ok(orderService.getShopOrdersByStatus(shopId, status));
    }

    // Shop owner updates order status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }
}