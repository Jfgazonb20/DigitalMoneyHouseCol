package com.Users.Users.Controller;

import com.Users.Users.DTO.LoginRequest;
import com.Users.Users.Exception.ResourceNotFoundException;
import com.Users.Users.Model.Role;
import com.Users.Users.Model.User;
import com.Users.Users.Security.JwtService;
import com.Users.Users.Security.TokenBlacklistService;
import com.Users.Users.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRoles(Collections.singleton(new Role("USER")));
    }

    @Test
    public void testRegisterUser_Success() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testRegisterUser_Failure() {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("El Username ya está registrado."));

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El Username ya está registrado.", response.getBody());
    }

    @Test
    public void testLoginUser_Success() {
        LoginRequest loginRequest = new LoginRequest("john", "password");

        when(userService.loginUser("john", "password")).thenReturn(user);
        when(jwtService.generateToken("john", 1L, List.of("USER"))).thenReturn("mockedToken");

        ResponseEntity<?> response = userController.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("token", "mockedToken");
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testLoginUser_Failure() {
        LoginRequest loginRequest = new LoginRequest("john", "wrongPassword");

        when(userService.loginUser("john", "wrongPassword"))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        ResponseEntity<?> response = userController.loginUser(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Contraseña incorrecta", response.getBody());
    }

    @Test
    public void testGetUserById_Success() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<?> response = userController.getUserById(1L, mockUserDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testGetUserById_Forbidden() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("otherUser");
        User otherUser = new User();
        otherUser.setId(2L);

        when(userService.findByUsername("otherUser")).thenReturn(otherUser);

        ResponseEntity<?> response = userController.getUserById(1L, mockUserDetails);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permiso para ver este perfil.", response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);

        User updatedUser = new User();
        updatedUser.setUsername("newUsername");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<?> response = userController.updateUser(1L, updatedUser, mockUserDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }


    @Test
    public void testLogoutUser_Success() {
        doNothing().when(tokenBlacklistService).invalidateToken(anyString());

        ResponseEntity<?> response = userController.logoutUser("Bearer mockedToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sesión cerrada correctamente. El token ha sido invalidado.", response.getBody());
    }

    @Test
    public void testLogoutUser_Failure() {
        ResponseEntity<?> response = userController.logoutUser(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token no válido.", response.getBody());
    }
}
