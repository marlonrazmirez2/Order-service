package com.tecsup.app.micro.product.service;

import com.tecsup.app.micro.product.client.User;
import com.tecsup.app.micro.product.client.UserClient;
import com.tecsup.app.micro.product.dto.Product;
import com.tecsup.app.micro.product.entity.ProductEntity;
import com.tecsup.app.micro.product.mapper.ProductMapper;
import com.tecsup.app.micro.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final UserClient userClient;

    public List<Product> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return productEntities.stream()
            .map(productEntity -> {
                try {
                    User user = userClient.getUserById(productEntity.getCreatedBy());
                    return mapper.toDomainWithUser(productEntity, user);
                } catch (Exception e) {
                    log.warn("Failed to get user for product {}: {}", productEntity.getId(), e.getMessage());
                    return mapper.toDomain(productEntity);
                }
            })
            .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            User user = userClient.getUserById(productEntity.getCreatedBy());
            return mapper.toDomainWithUser(productEntity, user);
        } catch (Exception e) {
            log.warn("Failed to get user for product {}: {}", id, e.getMessage());
            return mapper.toDomain(productEntity);
        }
    }

}
