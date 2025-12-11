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

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

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
        savedUser.setPassword("$2a$10$hashedPassword");
    }

    @Test
    void signUp_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO result = userService.signUp(validUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getUsername(), result.getUsername());
        
        verify(userRepository, times(1)).existsByEmail(validUserRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(validUserRequest.getPassword());
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
        String hashedPassword = "$2a$10$hashedPassword";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(validUserRequest.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Verify that password is set to hashed value
            assertEquals(hashedPassword, user.getPassword());
            assertNotEquals(validUserRequest.getPassword(), user.getPassword());
            return savedUser;
        });

        // Act
        userService.signUp(validUserRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode(validUserRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signUp_ShouldMapAllFieldsCorrectly() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
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
