package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user with a role
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");

        Role role = new Role();
        role.setName("ROLE_DRIVER");
        testUser.setRole(role);
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertFalse(authorities.isEmpty());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_DRIVER")));
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionWhenUserDoesNotExist() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistent")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}