package com.Users.Users.Controller;

import com.Users.Users.DTO.LoginRequest;
import com.Users.Users.Exception.ResourceNotFoundException;
import com.Users.Users.Model.User;
import com.Users.Users.Security.JwtService;
import com.Users.Users.Security.TokenBlacklistService;
import com.Users.Users.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api") // Este prefijo se mantiene
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            Long userId = user.getId(); // Asegúrate de que user.getId() devuelve un Long
            List<String> roles = user.getRoles()
                    .stream()
                    .map(role -> role.getName())
                    .toList();
            String token = jwtService.generateToken(user.getUsername(), userId, roles);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @GetMapping("/protected")
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok("Token válido. Usuario autenticado: " + userDetails.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o sesión cerrada.");
        }
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Comparar el ID autenticado con el ID solicitado
            User currentUser = userService.findByUsername(userDetails.getUsername());
            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver este perfil.");
            }
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Usuario no encontrado con ID: " + id);
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUserData,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Comparar el ID autenticado con el ID solicitado
            User currentUser = userService.findByUsername(userDetails.getUsername());
            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este perfil.");
            }
            User updatedUser = userService.updateUser(id, updatedUserData);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Usuario no encontrado con ID: " + id);
        }
    }

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.invalidateToken(token);
            return ResponseEntity.ok("Sesión cerrada correctamente. El token ha sido invalidado.");
        }
        return ResponseEntity.badRequest().body("Token no válido.");
    }
}
