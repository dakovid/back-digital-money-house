package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.User;
import com.digitalmoneyhouse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNombre("Juan");
        user.setApellido("Perez");
    }

    @Test
    void testGetUser_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User foundUser = userService.getUser(1L);
        assertNotNull(foundUser);
        assertEquals("Juan", foundUser.getNombre());
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User foundUser = userService.getUser(2L);
        assertNull(foundUser);
    }
}
