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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

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
        UserDto userDto = new UserDto();
        userDto.setDriver(new DriverDto());
        model.addAttribute("user", userDto);
        List<Role> roles = userService.findAllRoles();
        model.addAttribute("roles", roles);
        return "admin/register";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Model model) {

        if (!userDto.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/register";
        }

        if (userService.usernameExists(userDto.getUsername())) {
            result.rejectValue("username", "error.user", "El nombre de usuario ya está en uso");
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/register";
        }

        boolean isDriver = userService.isDriverRole(userDto.getRoleId());
        if (!isDriver) {
            userDto.setDriver(null);
        }

        userService.createUser(userDto, userDto.getRoleId());
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
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        UserDto userDto = userService.findUserById(id);

        if (userDto == null) {
            return "redirect:/admin/users?error=user_not_found";
        }

        if (userDto.getDriver() == null) {
            userDto.setDriver(new DriverDto());
        }

        userDto.setId(id);

        model.addAttribute("user", userDto);
        model.addAttribute("roles", userService.findAllRoles());

        return "admin/edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(
            @PathVariable("id") Long id,
            @ModelAttribute("user") UserDto userDto,
            BindingResult result,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        userDto.setId(id);

        if (userDto.getId() == null) {
            model.addAttribute("errorMessage", "ID de usuario no válido");
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/edit-user";
        }

        boolean updatePassword = userDto.getPassword() != null && !userDto.getPassword().isEmpty();

        if (updatePassword && !userDto.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "admin/edit-user";
        }

        boolean isDriver = userService.isDriverRole(userDto.getRoleId());

        if (!isDriver) {
            userDto.setDriver(null);
        } else if (userDto.getDriver() != null) {
            userDto.getDriver().setUserId(id);
        }

        try {
            userService.updateUser(id, userDto, updatePassword);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("roles", userService.findAllRoles());
            model.addAttribute("errorMessage", "Error al actualizar el usuario: " + e.getMessage());
            return "admin/edit-user";
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Estado del usuario actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cambiar el estado: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}