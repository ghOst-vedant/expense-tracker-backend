package com.expensetracker.service.impl;

import com.expensetracker.entity.User;
import com.expensetracker.exceptionHandler.EmailAlreadyExistsException;
import com.expensetracker.model.UserRequestDTO;
import com.expensetracker.model.UserResponseDTO;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO validUserRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validUserRequest = UserRequestDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword(new BCryptPasswordEncoder().encode("password123"));
    }

    @Test
    void signUp_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO result = userService.signUp(validUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getUsername(), result.getUsername());
        
        verify(userRepository, times(1)).existsByEmail(validUserRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signUp_WithDuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.signUp(validUserRequest)
        );

        assertTrue(exception.getMessage().contains(validUserRequest.getEmail()));
        verify(userRepository, times(1)).existsByEmail(validUserRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_ShouldHashPassword() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Verify that password is hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
            assertTrue(user.getPassword().startsWith("$2"));
            assertNotEquals(validUserRequest.getPassword(), user.getPassword());
            return savedUser;
        });

        // Act
        userService.signUp(validUserRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signUp_ShouldMapAllFieldsCorrectly() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(validUserRequest.getUsername(), user.getUsername());
            assertEquals(validUserRequest.getEmail(), user.getEmail());
            return savedUser;
        });

        // Act
        UserResponseDTO result = userService.signUp(validUserRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
