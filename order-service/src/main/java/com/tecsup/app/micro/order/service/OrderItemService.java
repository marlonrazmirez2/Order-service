package com.tecsup.app.micro.order.service;

import com.tecsup.app.micro.order.client.ProductClient;
import com.tecsup.app.micro.order.dto.OrderItem;
import com.tecsup.app.micro.order.dto.Product;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import com.tecsup.app.micro.order.mapper.OrderItemMapper;
import com.tecsup.app.micro.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ProductClient productClient;

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        List<OrderItemEntity> entities = orderItemRepository.findByOrderId(orderId);
        return entities.stream()
            .map(entity -> {
                Product product = productClient.getProductById(entity.getProductId());
                return orderItemMapper.toDomainWithProduct(entity, product);
            })
            .collect(Collectors.toList());
    }
}