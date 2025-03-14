package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import com.translogistics.fleetmanagmentsystem.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final DriverService driverService;

    public VehicleController(VehicleService vehicleService, DriverService driverService) {
        this.vehicleService = vehicleService;
        this.driverService = driverService;
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

            model.addAttribute("drivers", driverService.findAllDrivers());
            model.addAttribute("vehicle", vehicleDto);
            model.addAttribute("vehicleId", id);
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            model.addAttribute("isNewVehicle", false);

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

    @PostMapping("/assign-driver/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public String assignDriver(
            @PathVariable Long vehicleId,
            @RequestParam Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime assignmentStart,
            RedirectAttributes redirectAttributes) {
        try {
            vehicleService.assignDriver(vehicleId, driverId, assignmentStart);
            redirectAttributes.addFlashAttribute("successMessage", "Conductor asignado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al asignar conductor: " + e.getMessage());
        }
        return "redirect:/vehicles";
    }


    @GetMapping("/assign-driver-form")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String showAssignDriverForm(Model model) {
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        model.addAttribute("drivers", driverService.findAllDrivers());
        return "vehicles/assignDriver"; // Make sure this exactly matches the file name and path
    }




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