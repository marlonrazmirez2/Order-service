package com.tecsup.app.micro.order.mapper;

import com.tecsup.app.micro.order.dto.OrderItem;
import com.tecsup.app.micro.order.dto.Product;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItem toDomain(OrderItemEntity entity);

    OrderItemEntity toEntity(OrderItem domain);

    default OrderItem toDomainWithProduct(OrderItemEntity entity, Product product) {
        OrderItem orderItem = toDomain(entity);
        orderItem.setProduct(product);
        return orderItem;
    }
}