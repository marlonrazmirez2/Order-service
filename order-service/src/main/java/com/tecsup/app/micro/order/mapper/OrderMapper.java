package com.tecsup.app.micro.order.mapper;

import com.tecsup.app.micro.order.dto.Order;
import com.tecsup.app.micro.order.dto.OrderItem;
import com.tecsup.app.micro.order.dto.User;
import com.tecsup.app.micro.order.entity.OrderEntity;
import com.tecsup.app.micro.order.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toDomain(OrderEntity entity);

    OrderEntity toEntity(Order domain);

    OrderItem toDomain(OrderItemEntity entity);

    OrderItemEntity toEntity(OrderItem domain);

    List<OrderItem> toDomainList(List<OrderItemEntity> entities);

    List<OrderItemEntity> toEntityList(List<OrderItem> domains);

    default Order toDomainWithDetails(OrderEntity orderEntity, User user, List<OrderItem> items) {
        Order order = toDomain(orderEntity);
        order.setUser(user);
        order.setItems(items);
        return order;
    }
}