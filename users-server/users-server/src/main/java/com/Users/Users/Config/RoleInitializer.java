package com.Users.Users.Config;

import com.Users.Users.Model.Role;
import com.Users.Users.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Component
public class RoleInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initializeRoles() {
        createRoleIfNotExists("USER");
        createRoleIfNotExists("ADMIN");
    }

    private void createRoleIfNotExists(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(roleName);
            roleRepository.save(newRole);
            System.out.println("Rol " + roleName + " creado.");
        } else {
            System.out.println("Rol " + roleName + " ya existe.");
        }
    }
}
