package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.RoleRepository;
import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User createUser(UserDto userDto, Long roleId) {
        // Buscar el rol seleccionado
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true);
        user.setRole(role);

        return userRepository.save(user);
    }

    public User updateUser(Long userId, UserDto userDto, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo si hay cambios
        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            user.setUsername(userDto.getUsername());
        }

        // Actualizar contraseña solo si se proporciona una nueva
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Actualizar rol si se proporciona uno nuevo
        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cambiar el estado (activar/desactivar)
        user.setEnabled(!user.isEnabled());

        return userRepository.save(user);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}