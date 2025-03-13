package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
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
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       DriverRepository driverRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverRepository = driverRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        if (userDto.getRoleId() != null &&
                (user.getRole() == null || !user.getRole().getId().equals(userDto.getRoleId()))) {
            Role newRole = roleRepository.findById(userDto.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            user.setRole(newRole);
        }

        if (updatePassword) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if ("ROLE_DRIVER".equals(user.getRole().getName())) {
            Driver driver = user.getDriver();
            if (driver == null) {
                driver = new Driver();
                driver.setUser(user);
                user.setDriver(driver);
            }

            if (userDto.getDriver() != null) {
                driver.setLicenseNumber(userDto.getDriver().getLicenseNumber());
                driver.setExperienceYears(userDto.getDriver().getExperienceYears());
            }
        } else {
            if (user.getDriver() != null) {
                Driver driver = user.getDriver();
                user.setDriver(null);
                driverRepository.delete(driver);
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

    public boolean isDriverRole(Long roleId) {
        if (roleId == null) return false;
        Role role = roleRepository.findById(roleId).orElse(null);
        return role != null && "ROLE_DRIVER".equals(role.getName());
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}