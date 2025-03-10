package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String roleName =  user.getRole().getName();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roleName)
                .disabled(!user.isEnabled())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
