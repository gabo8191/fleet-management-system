//package com.translogistics.fleetmanagmentsystem.service;
//
//import com.translogistics.fleetmanagmentsystem.dto.UserDto;
//import com.translogistics.fleetmanagmentsystem.model.Role;
//import com.translogistics.fleetmanagmentsystem.model.User;
//import com.translogistics.fleetmanagmentsystem.repository.RoleRepository;
//import com.translogistics.fleetmanagmentsystem.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTests {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    private User testUser;
//    private Role testRole;
//    private UserDto testUserDto;
//
//    @BeforeEach
//    void setUp() {
//        testRole = new Role();
//        testRole.setId(1L);
//        testRole.setName("ROLE_DRIVER");
//
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUsername("testuser");
//        testUser.setPassword("encodedPassword");
//        testUser.setEnabled(true);
//        testUser.setRole(testRole);
//
//        testUserDto = new UserDto();
//        testUserDto.setUsername("testuser");
//        testUserDto.setPassword("password");
//    }
//
//    @Test
//    void findAllUsersShouldReturnListOfUsers() {
//        List<User> userList = new ArrayList<>();
//        userList.add(testUser);
//        when(userRepository.findAll()).thenReturn(userList);
//
//        List<User> result = userService.findAllUsers();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("testuser", result.get(0).getUsername());
//        verify(userRepository, times(1)).findAll();
//    }
//
//    @Test
//    void findByUsernameShouldReturnUserWhenExists() {
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
//
//        Optional<User> result = userService.findByUsername("testuser");
//
//        assertTrue(result.isPresent());
//        assertEquals("testuser", result.get().getUsername());
//        verify(userRepository, times(1)).findByUsername("testuser");
//    }
//
//    @Test
//    void findByUsernameShouldReturnEmptyWhenUserDoesNotExist() {
//        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
//
//        Optional<User> result = userService.findByUsername("nonexistent");
//
//        assertFalse(result.isPresent());
//        verify(userRepository, times(1)).findByUsername("nonexistent");
//    }
//
//    @Test
//    void findByIdShouldReturnUserWhenExists() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        Optional<User> result = userService.findById(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals(1L, result.get().getId());
//        verify(userRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void usernameExistsShouldReturnTrueWhenUsernameExists() {
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
//
//        boolean result = userService.usernameExists("testuser");
//
//        assertTrue(result);
//        verify(userRepository, times(1)).findByUsername("testuser");
//    }
//
//    @Test
//    void usernameExistsShouldReturnFalseWhenUsernameDoesNotExist() {
//        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
//
//        boolean result = userService.usernameExists("nonexistent");
//
//        assertFalse(result);
//        verify(userRepository, times(1)).findByUsername("nonexistent");
//    }
//
////    @Test
////    void createUserShouldSaveUserWithEncodedPasswordAndRole() {
////        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
////        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
////        when(userRepository.save(any(User.class))).thenReturn(testUser);
////
//////        User result = userService.createUser(testUserDto, 1L);
////
////        assertNotNull(result);
////        assertEquals("testuser", result.getUsername());
////        assertEquals("encodedPassword", result.getPassword());
////        assertTrue(result.isEnabled());
////        assertEquals("ROLE_DRIVER", result.getRole().getName());
////        verify(roleRepository, times(1)).findById(1L);
////        verify(passwordEncoder, times(1)).encode("password");
////        verify(userRepository, times(1)).save(any(User.class));
////    }
//
//    @Test
//    void createUserShouldThrowExceptionWhenRoleNotFound() {
//        when(roleRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                userService.createUser(testUserDto, 999L)
//        );
//
//        assertEquals("Error: Rol no encontrado", exception.getMessage());
//        verify(roleRepository, times(1)).findById(999L);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void updateUserShouldUpdateUsernamePasswordAndRole() {
//        UserDto updateDto = new UserDto();
//        updateDto.setUsername("updateduser");
//        updateDto.setPassword("newpassword");
//
//        Role newRole = new Role();
//        newRole.setId(2L);
//        newRole.setName("ROLE_ADMIN");
//
//        User existingUser = new User();
//        existingUser.setId(1L);
//        existingUser.setUsername("testuser");
//        existingUser.setPassword("oldEncodedPassword");
//        existingUser.setRole(testRole);
//        existingUser.setEnabled(true);
//
//        User updatedUser = new User();
//        updatedUser.setId(1L);
//        updatedUser.setUsername("updateduser");
//        updatedUser.setPassword("newEncodedPassword");
//        updatedUser.setRole(newRole);
//        updatedUser.setEnabled(true);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
//        when(roleRepository.findById(2L)).thenReturn(Optional.of(newRole));
//        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
//
//        User result = userService.updateUser(1L, updateDto, 2L);
//
//        assertNotNull(result);
//        assertEquals("updateduser", result.getUsername());
//        assertEquals("newEncodedPassword", result.getPassword());
//        assertEquals("ROLE_ADMIN", result.getRole().getName());
//        verify(userRepository, times(1)).findById(1L);
//        verify(roleRepository, times(1)).findById(2L);
//        verify(passwordEncoder, times(1)).encode("newpassword");
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void updateUserShouldThrowExceptionWhenUserNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                userService.updateUser(999L, testUserDto, 1L)
//        );
//
//        assertEquals("Usuario no encontrado", exception.getMessage());
//        verify(userRepository, times(1)).findById(999L);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void updateUserShouldThrowExceptionWhenRoleNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//        when(roleRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                userService.updateUser(1L, testUserDto, 999L)
//        );
//
//        assertEquals("Rol no encontrado", exception.getMessage());
//        verify(userRepository, times(1)).findById(1L);
//        verify(roleRepository, times(1)).findById(999L);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void toggleUserStatusShouldSwitchEnabledStatus() {
//        User enabledUser = new User();
//        enabledUser.setId(1L);
//        enabledUser.setUsername("testuser");
//        enabledUser.setEnabled(true);
//
//        User disabledUser = new User();
//        disabledUser.setId(1L);
//        disabledUser.setUsername("testuser");
//        disabledUser.setEnabled(false);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(enabledUser));
//        when(userRepository.save(any(User.class))).thenReturn(disabledUser);
//
//        User result = userService.toggleUserStatus(1L);
//
//        assertNotNull(result);
//        assertFalse(result.isEnabled());
//        verify(userRepository, times(1)).findById(1L);
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void toggleUserStatusShouldThrowExceptionWhenUserNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                userService.toggleUserStatus(999L)
//        );
//
//        assertEquals("Usuario no encontrado", exception.getMessage());
//        verify(userRepository, times(1)).findById(999L);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void findAllRolesShouldReturnListOfRoles() {
//        List<Role> roleList = new ArrayList<>();
//        roleList.add(testRole);
//        when(roleRepository.findAll()).thenReturn(roleList);
//
//        List<Role> result = userService.findAllRoles();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("ROLE_DRIVER", result.get(0).getName());
//        verify(roleRepository, times(1)).findAll();
//    }
//}