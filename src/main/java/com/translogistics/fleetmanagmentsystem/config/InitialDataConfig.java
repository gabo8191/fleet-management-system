package com.translogistics.fleetmanagmentsystem.config;

import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.RoleRepository;
import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class InitialDataConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverRepository driverRepository;

    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.role}")
    private String adminRoleName;

    @Value("${app.dispatcher.username}")
    private String dispatcherUsername;
    @Value("${app.dispatcher.password}")
    private String dispatcherPassword;
    @Value("${app.dispatcher.role}")
    private String dispatcherRoleName;

    @Value("${app.driver.username}")
    private String driverUsername;
    @Value("${app.driver.password}")
    private String driverPassword;
    @Value("${app.driver.role}")
    private String driverRoleName;

    @Value("${app.mechanic.username}")
    private String mechanicUsername;
    @Value("${app.mechanic.password}")
    private String mechanicPassword;
    @Value("${app.mechanic.role}")
    private String mechanicRoleName;

    public InitialDataConfig(RoleRepository roleRepository, UserRepository userRepository, DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            createUserForRole(adminRoleName, adminUsername, adminPassword, "Administrador del sistema");
            createUserForRole(dispatcherRoleName, dispatcherUsername, dispatcherPassword, "Despachador");
            createUserForRole(driverRoleName, driverUsername, driverPassword, "Conductor");
            createUserForRole(mechanicRoleName, mechanicUsername, mechanicPassword, "Mecánico");
        };
    }

    private void createUserForRole(String roleName, String username, String password, String roleDescription) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription(roleDescription);
                    newRole.setCreatedAt(LocalDateTime.now());
                    newRole.setUpdatedAt(LocalDateTime.now());
                    return roleRepository.save(newRole);
                });

        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setFirstName(username);
            user.setLastName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            if (roleName.equals("ROLE_DRIVER")) {
                User savedUser = userRepository.save(user);
                Driver driver = new Driver();
                driver.setLicenseNumber("123456");
                driver.setExperienceYears(5);
                driver.setUser(savedUser);
                driverRepository.save(driver);
            } else {
                userRepository.save(user);
            }
        }
    }
}
