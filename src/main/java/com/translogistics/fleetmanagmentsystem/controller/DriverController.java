package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.findAllDrivers());
        return "drivers/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new DriverDto());
        return "drivers/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createDriver(
            @Valid @ModelAttribute("driver") DriverDto driverDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "drivers/create";
        }

        if (driverService.licenseNumberExists(driverDto.getLicenseNumber())) {
            result.rejectValue("licenseNumber", "error.driver", "La licencia ya está registrada");
            return "drivers/create";
        }

        driverService.createDriver(driverDto);
        return "redirect:/drivers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Driver driver = driverService.findById(id);

        DriverDto driverDto = new DriverDto();
        driverDto.setName(driver.getName());
        driverDto.setLastName(driver.getLastName());
        driverDto.setLicenseNumber(driver.getLicenseNumber());
        driverDto.setExperienceYears(driver.getExperienceYears());

        model.addAttribute("driver", driverDto);
        model.addAttribute("driverId", id);
        return "drivers/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateDriver(
            @PathVariable Long id,
            @Valid @ModelAttribute("driver") DriverDto driverDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("driverId", id);
            return "drivers/edit";
        }

        driverService.updateDriver(id, driverDto);
        return "redirect:/drivers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return "redirect:/drivers";
    }
}