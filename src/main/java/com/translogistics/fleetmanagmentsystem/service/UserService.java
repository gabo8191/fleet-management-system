package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.RoleRepository;
import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    public void createUser(UserDto userDto, Long roleId) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        user.setRole(role);

        if (role.getName().equals("ROLE_DRIVER") && userDto.getDriver() != null) {
            Driver driver = new Driver();
            driver.setLicenseNumber(userDto.getDriver().getLicenseNumber());
            driver.setExperienceYears(userDto.getDriver().getExperienceYears());
            driver.setUser(user);
            user.setDriver(driver);
        }

        userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        if (user.getRole() != null) {
            userDto.setRoleId(user.getRole().getId());
        }

        if (user.getDriver() != null) {
            DriverDto driverDto = new DriverDto();
            driverDto.setLicenseNumber(user.getDriver().getLicenseNumber());
            driverDto.setExperienceYears(user.getDriver().getExperienceYears());
            userDto.setDriver(driverDto);
        }

        return userDto;
    }

    @Transactional
    public void updateUser(Long id, UserDto userDto, boolean updatePassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        if (updatePassword) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        user.setRole(role);

        if (role.getName().equals("ROLE_DRIVER")) {
            if (user.getDriver() != null) {
                user.getDriver().setLicenseNumber(userDto.getDriver().getLicenseNumber());
                user.getDriver().setExperienceYears(userDto.getDriver().getExperienceYears());
            } else {
                Driver driver = new Driver();
                driver.setLicenseNumber(userDto.getDriver().getLicenseNumber());
                driver.setExperienceYears(userDto.getDriver().getExperienceYears());
                driver.setUser(user);
                user.setDriver(driver);
            }
        } else {
            if (user.getDriver() != null) {
                user.setDriver(null);
            }
        }

        userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setEnabled(!user.isEnabled());

        return userRepository.save(user);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}