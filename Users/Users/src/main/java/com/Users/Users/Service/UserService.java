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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionsFeignClient transactionsFeignClient;

    public User registerUser(User user) {
        try {
            // Validar existencia previa de username y DNI
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("El Username ya está registrado.");
            }
            if (userRepository.existsByDni(user.getDni())) {
                throw new IllegalArgumentException("El DNI ya está registrado.");
            }

            // Generar fullName si no existe
            if (user.getFullName() == null || user.getFullName().isEmpty()) {
                if (user.getFirstName() != null && user.getLastName() != null) {
                    user.setFullName(user.getFirstName() + " " + user.getLastName());
                } else {
                    throw new IllegalArgumentException("El nombre y apellido son obligatorios.");
                }
            }

            // Asignar roles desde la solicitud si se proporciona
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                Set<Role> userRoles = user.getRoles().stream()
                        .map(role -> roleRepository.findByName(role.getName())
                                .orElseThrow(() -> new RuntimeException("Rol " + role.getName() + " no encontrado.")))
                        .collect(Collectors.toSet());
                user.setRoles(userRoles);
            } else {
                // Asignar rol por defecto
                Role userRole = roleRepository.findByName("USER")
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado."));
                user.setRoles(Set.of(userRole));
            }

            // Encriptar contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Generar CVU y alias
            user.setCvu(AliasGenerator.generateCvu());
            user.setAlias(AliasGenerator.generateAlias());

            // Guardar usuario en la base de datos de users-service
            User savedUser = userRepository.save(user);

            // Crear cuenta asociada en transactions-service
            AccountRequest accountRequest = new AccountRequest(savedUser.getId(), user.getCvu(), 0.0);
            transactionsFeignClient.createAccount(accountRequest);

            return savedUser;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error interno al registrar el usuario.");
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public User updateUser(Long id, User updatedUserData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Validar y actualizar los campos que se quieren modificar
        if (updatedUserData.getEmail() != null && !updatedUserData.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updatedUserData.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("El email ya está registrado.");
            }
            user.setEmail(updatedUserData.getEmail());
        }

        if (updatedUserData.getUsername() != null && !updatedUserData.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(updatedUserData.getUsername()).isPresent()) {
                throw new UserAlreadyExistsException("El nombre de usuario ya existe.");
            }
            user.setUsername(updatedUserData.getUsername());
        }

        // Actualizar contraseña (si aplica)
        if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
        }

        // Guardar los cambios
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        return user;
    }
}
