package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.dto.MaintenanceDto;
import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import com.translogistics.fleetmanagmentsystem.service.MaintenanceService;
import com.translogistics.fleetmanagmentsystem.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final MaintenanceService maintenanceService;

    public VehicleController(VehicleService vehicleService, DriverService driverService, MaintenanceService maintenanceService) {
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.maintenanceService = maintenanceService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        return "vehicles/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new VehicleDto());
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("vehicleStatuses", VehicleStatus.values());
        return "vehicles/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createVehicle(
            @Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/create";
        }

        if (vehicleService.licensePlateExists(vehicleDto.getLicensePlate())) {
            result.rejectValue("licensePlate", "error.vehicle", "La placa ya está registrada");
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/create";
        }

        vehicleService.createVehicle(vehicleDto);
        return "redirect:/vehicles";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setLicensePlate(vehicle.getLicensePlate());
        vehicleDto.setModel(vehicle.getModel());
        vehicleDto.setYear(vehicle.getYear());
        vehicleDto.setType(vehicle.getType());
        vehicleDto.setStatus(vehicle.getStatus());

        // Añadir lista de conductores disponibles
        model.addAttribute("drivers", driverService.findAllDrivers());
        model.addAttribute("vehicle", vehicleDto);
        model.addAttribute("vehicleId", id);
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("vehicleStatuses", VehicleStatus.values());

        return "vehicles/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateVehicle(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/edit";
        }

        vehicleService.updateVehicle(id, vehicleDto);
        return "redirect:/vehicles";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/toggle-status/{id}")
    public String toggleVehicleStatus(@PathVariable Long id) {
        vehicleService.toggleVehicleStatus(id);
        return "redirect:/vehicles";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') ")
    @PostMapping("/assign-driver/{vehicleId}")
    public String assignDriver(
            @PathVariable Long vehicleId,
            @RequestParam Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime assignmentStart
    ) {
        vehicleService.assignDriver(vehicleId, driverId, assignmentStart);
        return "redirect:/vehicles";
    }

    // Agregar estos métodos al controlador
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')") // Solo Admin/Mecánico
    @GetMapping("/{id}/maintenances")
    public String viewMaintenanceHistory(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("maintenances", maintenanceService.findMaintenancesByVehicleId(id));
        return "vehicles/maintenance-history"; // Vista del historial
    }

    @PreAuthorize("hasRole('MECHANIC')") // Solo Mecánico
    @GetMapping("/{id}/schedule-maintenance")
    public String showScheduleMaintenanceForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicleId", id);
        model.addAttribute("maintenance", new MaintenanceDto());
        return "vehicles/schedule-maintenance"; // Formulario de programación
    }

    @PreAuthorize("hasRole('MECHANIC')")
    @PostMapping("/{id}/schedule-maintenance")
    public String scheduleMaintenance(
            @PathVariable Long id,
            @Valid @ModelAttribute("maintenance") MaintenanceDto dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "vehicles/schedule-maintenance";
        }
        maintenanceService.logMaintenance(id, dto); // Asocia el mantenimiento al vehículo
        return "redirect:/vehicles/" + id + "/maintenances";
    }
}