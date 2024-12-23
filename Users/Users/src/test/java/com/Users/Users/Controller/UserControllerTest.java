package com.Users.Users.Controller;

import com.Users.Users.DTO.LoginRequest;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        // Configurar datos
        User user = new User();
        user.setUsername("pipe");
        user.setEmail("pipe@example.com");
        user.setPassword("pipe123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("pipe");
        savedUser.setEmail("pipe@example.com");

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);

        // Ejecutar prueba
        ResponseEntity<?> response = userController.registerUser(user);

        // Verificar
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
    }

    @Test
    public void testRegisterUser_DuplicateUsername() {
        // Configurar excepción
        User user = new User();
        user.setUsername("pipe");
        user.setEmail("pipe@example.com");
        user.setPassword("pipe123");

        when(userService.registerUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("El Username ya está registrado."));

        // Ejecutar prueba
        ResponseEntity<?> response = userController.registerUser(user);

        // Verificar
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El Username ya está registrado.", response.getBody());
    }
    @Test
    public void testLoginUser_Success() {
        // Configurar datos
        LoginRequest loginRequest = new LoginRequest("pipe", "pipe123");
        User user = new User();
        user.setUsername("pipe");

        when(userService.loginUser("pipe", "pipe123")).thenReturn(user);
        when(jwtService.generateToken("pipe")).thenReturn("mockedToken");

        // Ejecutar prueba
        ResponseEntity<?> response = userController.loginUser(loginRequest);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> expected = new HashMap<>();
        expected.put("token", "mockedToken");
        assertEquals(expected, response.getBody());
    }

    @Test
    public void testLoginUser_Failure() {
        // Configurar excepción
        LoginRequest loginRequest = new LoginRequest("pipe", "wrongPassword");

        when(userService.loginUser("pipe", "wrongPassword"))
                .thenThrow(new RuntimeException("Contraseña incorrecta."));

        // Ejecutar prueba
        ResponseEntity<?> response = userController.loginUser(loginRequest);

        // Verificar
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Contraseña incorrecta.", response.getBody());
    }

    @Test
    public void testValidateToken_Success() {
        // Configurar token válido
        String validToken = "mockedToken";
        String username = "pipe";

        UserDetails mockUserDetails = org.mockito.Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(username);

        // Simular solicitud protegida
        ResponseEntity<?> response = userController.validateToken(mockUserDetails);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token válido. Usuario autenticado: pipe", response.getBody());
    }


    @Test
    public void testLogoutUser_Success() {
        // Configurar invalidación del token
        String token = "mockedToken";
        when(tokenBlacklistService.isTokenInvalid(token)).thenReturn(false);

        // Ejecutar prueba
        ResponseEntity<?> response = userController.logoutUser("Bearer " + token);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sesión cerrada correctamente. El token ha sido invalidado.", response.getBody());
    }
}
