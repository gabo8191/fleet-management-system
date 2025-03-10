package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private List<Role> roles;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        roles = new ArrayList<>();
        Role driverRole = new Role();
        driverRole.setId(1L);
        driverRole.setName("ROLE_DRIVER");
        roles.add(driverRole);

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");
        roles.add(adminRole);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);
        testUser.setRole(driverRole);
    }

    @Test
    void loginShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void registerFormShouldAddUserAndRolesToModelAndReturnRegisterView() throws Exception {
        when(userService.findAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(view().name("auth/register"));

        verify(userService, times(1)).findAllRoles();
    }

    @Test
    void registerShouldReturnRegisterViewWhenValidationErrors() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        Long roleId = 1L;

        when(bindingResult.hasErrors()).thenReturn(true);
        when(userService.findAllRoles()).thenReturn(roles);

        String viewName = userController.register(userDto, bindingResult, roleId, model);

        assertEquals("auth/register", viewName);
        verify(userService, never()).createUser(any(UserDto.class), anyLong());
        verify(model, times(1)).addAttribute(eq("roles"), anyList());
    }

    @Test
    void registerShouldReturnRegisterViewWhenUsernameExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        Long roleId = 1L;

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.usernameExists("testuser")).thenReturn(true);
        when(userService.findAllRoles()).thenReturn(roles);

        String viewName = userController.register(userDto, bindingResult, roleId, model);

        assertEquals("auth/register", viewName);
        verify(userService, never()).createUser(any(UserDto.class), anyLong());
        verify(bindingResult, times(1)).rejectValue(eq("username"), eq("error.user"), anyString());
        verify(model, times(1)).addAttribute(eq("roles"), anyList());
    }

    @Test
    void registerShouldCreateUserAndRedirectToUsersListWhenValid() {
        UserDto userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password");
        Long roleId = 1L;

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.usernameExists("newuser")).thenReturn(false);
        when(userService.createUser(userDto, roleId)).thenReturn(testUser);

        String viewName = userController.register(userDto, bindingResult, roleId, model);

        assertEquals("redirect:/admin/users", viewName);
        verify(userService, times(1)).createUser(userDto, roleId);
    }

    @Test
    void listUsersShouldAddUsersToModelAndReturnUserListView() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("admin/users"));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void editUserFormShouldAddUserAndRolesToModelAndReturnEditView() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(userService.findAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("currentRole"))
                .andExpect(view().name("admin/edit-user"));

        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).findAllRoles();
    }

    @Test
    void editUserFormShouldThrowExceptionWhenUserNotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(get("/admin/users/edit/999"));
        } catch (Exception e) {
            assertEquals("Usuario no encontrado", e.getCause().getMessage());
        }

        verify(userService, times(1)).findById(999L);
    }

    @Test
    void updateUserShouldReturnEditViewWhenValidationErrors() {
        UserDto userDto = new UserDto();
        userDto.setUsername("updateduser");
        userDto.setPassword("newpassword");
        Long roleId = 2L;
        Long userId = 1L;

        when(bindingResult.hasErrors()).thenReturn(true);
        when(userService.findAllRoles()).thenReturn(roles);

        String viewName = userController.updateUser(userId, userDto, bindingResult, roleId, model);

        assertEquals("admin/edit-user", viewName);
        verify(userService, never()).updateUser(anyLong(), any(UserDto.class), anyLong());
        verify(model, times(1)).addAttribute(eq("roles"), anyList());
        verify(model, times(1)).addAttribute("userId", userId);
    }

    @Test
    void updateUserShouldUpdateUserAndRedirectToUsersListWhenValid() {
        UserDto userDto = new UserDto();
        userDto.setUsername("updateduser");
        userDto.setPassword("newpassword");
        Long roleId = 2L;
        Long userId = 1L;

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateUser(userId, userDto, roleId)).thenReturn(testUser);

        String viewName = userController.updateUser(userId, userDto, bindingResult, roleId, model);

        assertEquals("redirect:/admin/users", viewName);
        verify(userService, times(1)).updateUser(userId, userDto, roleId);
    }

    @Test
    void toggleUserStatusShouldToggleStatusAndRedirectToUsersList() throws Exception {
        when(userService.toggleUserStatus(1L)).thenReturn(testUser);

        mockMvc.perform(get("/admin/users/toggle/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).toggleUserStatus(1L);
    }
}