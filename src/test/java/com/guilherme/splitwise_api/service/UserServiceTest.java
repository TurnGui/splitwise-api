package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.exception.ResourceNotFoundException;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldEncryptPasswordBeforeSaving() {
        User user = new User();
        user.setName("Gui");
        user.setEmail("gui@example.com");
        user.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("hashed-password");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("hashed-password", result.getPassword());
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("Gui");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("Gui", result.getName());
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }
}