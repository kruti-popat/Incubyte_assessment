package com.dealership.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dealership.inventory.dto.AuthResponse;
import com.dealership.inventory.dto.LoginRequest;
import com.dealership.inventory.dto.RegisterRequest;
import com.dealership.inventory.exception.BadRequestException;
import com.dealership.inventory.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void register_createsUserAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("newuser" + System.nanoTime() + "@example.com");
        request.setPassword("password123");

        AuthResponse response = authService.register(request);

        assertNotNull(response.getToken());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void register_throwsWhenEmailExists() {
        String email = "duplicate" + System.nanoTime() + "@example.com";

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail(email);
        request.setPassword("password123");
        authService.register(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setName("Another User");
        duplicate.setEmail(email);
        duplicate.setPassword("password456");

        assertThrows(BadRequestException.class, () -> authService.register(duplicate));
    }

    @Test
    void login_returnsToken() {
        String email = "loginuser" + System.nanoTime() + "@example.com";
        String password = "password123";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Login User");
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals(email, response.getEmail());
    }
}
