package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.User;
import com.digitalmoneyhouse.repository.UserRepository;
import com.digitalmoneyhouse.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
    }

    @Test
    void testAuthenticate_Success() throws AuthService.UserNotFoundException, AuthService.IncorrectPasswordException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("mockedToken");

        String token = authService.authenticate("test@example.com", "password123");
        assertNotNull(token);
        assertEquals("mockedToken", token);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        assertThrows(AuthService.UserNotFoundException.class, () ->
            authService.authenticate("notfound@example.com", "password123")
        );
    }
}
