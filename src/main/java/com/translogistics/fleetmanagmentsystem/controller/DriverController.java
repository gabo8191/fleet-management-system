package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.findAllDrivers());
        return "drivers/listDriver";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new DriverDto());
        return "drivers/create-driver";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createDriver(
            @Valid @ModelAttribute("driver") DriverDto driverDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "drivers/create-driver";
        }

        if (driverService.licenseNumberExists(driverDto.getLicenseNumber())) {
            result.rejectValue("licenseNumber", "error.driver", "El número de licencia ya está registrado");
            return "drivers/create-driver";
        }

        driverService.createDriver(driverDto);
        redirectAttributes.addFlashAttribute("successMessage", "Conductor creado exitosamente");
        return "redirect:/drivers";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Driver driver = driverService.findById(id);
            DriverDto driverDto = convertToDto(driver);
            model.addAttribute("driver", driverDto);
            return "drivers/edit-driver";
        } catch (DriverNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateDriver(
            @PathVariable Long id,
            @Valid @ModelAttribute("driver") DriverDto driverDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "drivers/edit-driver";
        }

        try {
            driverService.updateDriver(id, driverDto);
            redirectAttributes.addFlashAttribute("successMessage", "Conductor actualizado exitosamente");
            return "redirect:/drivers";
        } catch (DriverNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDriver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            driverService.deleteDriver(id);
            redirectAttributes.addFlashAttribute("successMessage", "Conductor eliminado exitosamente");
        } catch (DriverNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/drivers";
    }

    private DriverDto convertToDto(Driver driver) {
        DriverDto driverDto = new DriverDto();
        driverDto.setLicenseNumber(driver.getLicenseNumber());
        driverDto.setExperienceYears(driver.getExperienceYears());
        return driverDto;
    }
}