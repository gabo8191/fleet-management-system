package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.UserDto;
import com.translogistics.fleetmanagmentsystem.model.Role;
import com.translogistics.fleetmanagmentsystem.model.User;
import com.translogistics.fleetmanagmentsystem.service.UserService;
import jakarta.validation.Valid;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/admin/users";
        }
        return "auth/login";
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());

        model.addAttribute("user", userDto);
        model.addAttribute("userId", id);
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("currentRole", user.getRole());

        return "admin/edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            @RequestParam("roleId") Long roleId,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            model.addAttribute("userId", id);
            return "admin/edit-user";
        }

        userService.updateUser(id, userDto, roleId);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }
}