package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "auth/login";
    }

    @GetMapping({"", "/", "/home"})
    public String home() {
        return "home";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserDto());
        List<Role> roles = userService.findAllRoles();
        model.addAttribute("roles", roles);
        return "admin/register";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") UserDto userDto,
            @Valid @ModelAttribute("driver") DriverDto driverDto,
            BindingResult result,
            @RequestParam("roleId") Long roleId,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/register";
        }

        if (userService.usernameExists(userDto.getUsername())) {
            result.rejectValue("username", "error.user", "El nombre de usuario ya está en uso");
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/register";
        }

        userService.createUser(userDto, roleId);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserDto userDto = userService.findUserById(id);

        if (userDto == null) {
            return "redirect:/admin/users?error=user_not_found";
        }

        model.addAttribute("user", userDto);
        model.addAttribute("roles", userService.findAllRoles());

        return "admin/edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/edit-user";
        }

        boolean updatePassword = userDto.getPassword() != null && !userDto.getPassword().isEmpty();

        try {
            userService.updateUser(id, userDto, updatePassword);
            return "redirect:/admin/users?success=user_updated";
        } catch (Exception e) {
            model.addAttribute("roles", userService.findAllRoles());
            model.addAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
            return "admin/edit-user";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }
}