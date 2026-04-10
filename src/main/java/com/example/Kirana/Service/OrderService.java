package com.example.Kirana.Service;

import com.example.Kirana.DTOs.Requests.*;
import com.example.Kirana.DTOs.Response.*;
import com.example.Kirana.Entity.*;
import com.example.Kirana.Enums.*;
import com.example.Kirana.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    // Customer creates an order
    public OrderResponse createOrder(String customerId, CreateOrderRequest request) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Order order = Order.builder()
                .customer(customer)
                .shop(shop)
                .status(OrderStatus.PENDING)
                .customerNote(request.getCustomerNote())
                .paymentMode(PaymentMode.valueOf(
                    request.getPaymentMode() != null ? request.getPaymentMode() : "CASH"))
                .build();

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                        "Product not found: " + itemReq.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .itemName(product.getName())      // auto-fill from product
                    .quantity(itemReq.getQuantity())
                    .note(itemReq.getNote())
                    .unitPrice(product.getPricePerUnit())
                    .isAvailable(true)
                    .build();

        }).collect(Collectors.toList());

        order.setItems(items);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    // Customer views their orders
    public List<OrderResponse> getCustomerOrders(String customerId) {
        return orderRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Shop owner views incoming orders
    public List<OrderResponse> getShopOrders(String shopId) {
        return orderRepository
                .findByShopIdOrderByCreatedAtDesc(shopId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Shop owner views orders by status
    public List<OrderResponse> getShopOrdersByStatus(String shopId, String status) {
        return orderRepository
                .findByShopIdAndStatus(shopId, OrderStatus.valueOf(status))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Shop owner updates order status
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(request.getStatus()));

        if (request.getRejectionReason() != null) {
            order.setRejectionReason(request.getRejectionReason());
        }

        orderRepository.save(order);
        return mapToResponse(order);
    }

    // Map entity to response DTO
    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .note(item.getNote())
                        .isAvailable(item.getIsAvailable())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .shopId(order.getShop().getId())
                .shopName(order.getShop().getName())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .status(order.getStatus().name())
                .paymentMode(order.getPaymentMode().name())
                .totalAmount(order.getTotalAmount())
                .customerNote(order.getCustomerNote())
                .rejectionReason(order.getRejectionReason())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}