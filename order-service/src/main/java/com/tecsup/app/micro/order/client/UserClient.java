package com.tecsup.app.micro.order.client;

import com.tecsup.app.micro.order.dto.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public User getUserById(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        User user = restTemplate.getForObject(url, User.class);
        log.info("User retrieved successfully: {}", user);
        return user;
    }

    private User getUserByIdFallback(Long userId, Throwable throwable) {
        log.warn("Fallback method invoked for getUserById due to: {}", throwable.getMessage());
        return new User(userId, "Unknown User", "unknown@example.com", "999-999-999", "Unknown Address");
    }
}