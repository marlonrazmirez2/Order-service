package com.tecsup.app.micro.order.client;

import com.tecsup.app.micro.order.dto.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    public Product getProductById(Long productId) {
        String url = productServiceUrl + "/api/products/" + productId;
        Product product = restTemplate.getForObject(url, Product.class);
        log.info("Product retrieved successfully: {}", product);
        return product;
    }

    private Product getProductByIdFallback(Long productId, Throwable throwable) {
        log.warn("Fallback method invoked for getProductById due to: {}", throwable.getMessage());
        return new Product(productId, "Unknown Product", java.math.BigDecimal.ZERO);
    }
}