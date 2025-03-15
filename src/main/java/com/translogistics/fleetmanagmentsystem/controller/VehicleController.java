package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import com.translogistics.fleetmanagmentsystem.service.DriverVehicleAssignmentService;
import com.translogistics.fleetmanagmentsystem.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final DriverVehicleAssignmentService assignmentService;

    public VehicleController(
            VehicleService vehicleService,
            DriverService driverService,
            DriverVehicleAssignmentService assignmentService) {
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.assignmentService = assignmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        return "vehicles/vehicles";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new VehicleDto());
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("vehicleStatuses", VehicleStatus.values());
        return "vehicles/create-vehicle";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createVehicle(
            @Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            model.addAttribute("isNewVehicle", true);
            return "vehicles/create-vehicle";
        }

        if (vehicleService.licensePlateExists(vehicleDto.getLicensePlate())) {
            result.rejectValue("licensePlate", "error.vehicle", "La placa ya está registrada");
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            model.addAttribute("isNewVehicle", true);
            return "vehicles/create-vehicle";
        }

        vehicleService.createVehicle(vehicleDto);
        redirectAttributes.addFlashAttribute("successMessage", "Vehículo creado exitosamente");
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Vehicle vehicle = vehicleService.findById(id)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehículo con ID " + id + " no encontrado"));

            VehicleDto vehicleDto = convertToDto(vehicle);

            model.addAttribute("vehicle", vehicleDto);
            model.addAttribute("vehicleId", id);
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            model.addAttribute("isNewVehicle", false);

            // Add current assignments for this vehicle, if any
            model.addAttribute("assignments", assignmentService.findAssignmentsByVehicle(id));
            // Add available drivers for potential assignment
            model.addAttribute("drivers", driverService.findAllDrivers());

            return "vehicles/edit-vehicle";
        } catch (VehicleNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateVehicle(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            model.addAttribute("isNewVehicle", false);
            return "vehicles/edit-vehicle";
        }

        try {
            vehicleService.updateVehicle(id, vehicleDto);
            redirectAttributes.addFlashAttribute("successMessage", "Vehículo actualizado exitosamente");
            return "redirect:/vehicles";
        } catch (VehicleNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehículo eliminado exitosamente");
        } catch (VehicleNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vehicles";
    }

    @GetMapping("/toggle-status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleVehicleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.toggleVehicleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Estado del vehículo actualizado");
        } catch (VehicleNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vehicles";
    }

    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String viewVehicleAssignments(@PathVariable Long id, Model model) {
        try {
            Vehicle vehicle = vehicleService.findById(id)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

            model.addAttribute("vehicle", vehicle);
            model.addAttribute("assignments", assignmentService.findAssignmentsByVehicle(id));
            return "vehicles/vehicle-assignments";
        } catch (VehicleNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    // Removed the duplicate assignDriver methods - now they're centralized in DriverVehicleAssignmentController

    private VehicleDto convertToDto(Vehicle vehicle) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setLicensePlate(vehicle.getLicensePlate());
        vehicleDto.setModel(vehicle.getModel());
        vehicleDto.setYear(vehicle.getYear());
        vehicleDto.setType(vehicle.getType());
        vehicleDto.setStatus(vehicle.getStatus());
        return vehicleDto;
    }
}