package com.tecsup.app.micro.order.service;

import com.tecsup.app.micro.order.client.ProductClient;
import com.tecsup.app.micro.order.client.UserClient;
import com.tecsup.app.micro.order.dto.*;
import com.tecsup.app.micro.order.entity.OrderEntity;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import com.tecsup.app.micro.order.mapper.OrderItemMapper;
import com.tecsup.app.micro.order.mapper.OrderMapper;
import com.tecsup.app.micro.order.repository.OrderItemRepository;
import com.tecsup.app.micro.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserClient userClient;
    private final ProductClient productClient;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Validate user
        User user = userClient.getUserById(request.getUserId());

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Calculate total and create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = request.getItems().stream()
            .map(itemRequest -> {
                Product product = productClient.getProductById(itemRequest.getProductId());
                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setProductId(itemRequest.getProductId());
                itemEntity.setQuantity(itemRequest.getQuantity());
                itemEntity.setUnitPrice(product.getPrice());
                itemEntity.setSubtotal(subtotal);

                return itemEntity;
            })
            .collect(Collectors.toList());

        totalAmount = orderItems.stream()
            .map(OrderItemEntity::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order entity
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNumber(orderNumber);
        orderEntity.setUserId(request.getUserId());
        orderEntity.setStatus("PENDING");
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setUpdatedAt(LocalDateTime.now());

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        // Save order items
        orderItems.forEach(item -> item.setOrderId(savedOrder.getId()));
        List<OrderItemEntity> savedItems = orderItemRepository.saveAll(orderItems);

        // Build response
        List<OrderItem> orderItemDtos = savedItems.stream()
            .map(item -> {
                Product product = productClient.getProductById(item.getProductId());
                return orderItemMapper.toDomainWithProduct(item, product);
            })
            .collect(Collectors.toList());

        return orderMapper.toDomainWithDetails(savedOrder, user, orderItemDtos);
    }

    public Order getOrderById(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userClient.getUserById(orderEntity.getUserId());

        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrderId(id);
        List<OrderItem> orderItems = orderItemEntities.stream()
            .map(item -> {
                Product product = productClient.getProductById(item.getProductId());
                return orderItemMapper.toDomainWithProduct(item, product);
            })
            .collect(Collectors.toList());

        return orderMapper.toDomainWithDetails(orderEntity, user, orderItems);
    }

    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
}