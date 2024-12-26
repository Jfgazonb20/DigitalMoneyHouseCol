package com.Users.Users.Service;

import com.Users.Users.Client.TransactionsFeignClient;
import com.Users.Users.DTO.AccountRequest;
import com.Users.Users.Exception.ResourceNotFoundException;
import com.Users.Users.Exception.UserAlreadyExistsException;
import com.Users.Users.Model.Role;
import com.Users.Users.Model.User;
import com.Users.Users.Repository.RoleRepository;
import com.Users.Users.Repository.UserRepository;
import com.Users.Users.Util.AliasGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TransactionsFeignClient transactionsFeignClient;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setDni("12345678");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void testRegisterUser_Success() {
        Role userRole = new Role();
        userRole.setName("USER");

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(transactionsFeignClient).createAccount(any(AccountRequest.class));

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(transactionsFeignClient, times(1)).createAccount(any(AccountRequest.class));
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

        assertEquals("El Username ya está registrado.", exception.getMessage());
    }

    @Test
    void testRegisterUser_MissingName() {
        user.setFirstName(null);
        user.setLastName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));

        assertEquals("El nombre y apellido son obligatorios.", exception.getMessage());
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser("testUser", "password123"));

        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void testLoginUser_PasswordIncorrect() {
        user.setPassword("encodedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.loginUser("testUser", "wrongPassword"));

        assertEquals("Contraseña incorrecta.", exception.getMessage());
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        User updatedUserData = new User();
        updatedUserData.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updatedUserData));

        assertEquals("El email ya está registrado.", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        User updatedUserData = new User();
        updatedUserData.setEmail("newemail@example.com");
        updatedUserData.setUsername("newUser");
        updatedUserData.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updatedUserData);

        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("newUser", result.getUsername());
        assertEquals("encodedNewPassword", result.getPassword());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));

        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }
}
