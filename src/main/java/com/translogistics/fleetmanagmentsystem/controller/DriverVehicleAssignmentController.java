package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.exceptions.AssignmentNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.service.DriverVehicleAssignmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/assignments")
public class DriverVehicleAssignmentController {

    private final DriverVehicleAssignmentService assignmentService;

    public DriverVehicleAssignmentController(DriverVehicleAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String assignDriver(
            @RequestParam Long vehicleId,
            @RequestParam Long driverId,
            @RequestParam LocalDateTime assignmentStart,
            RedirectAttributes redirectAttributes) {
        try {
            assignmentService.assignDriver(vehicleId, driverId, assignmentStart);
            redirectAttributes.addFlashAttribute("successMessage", "Conductor asignado exitosamente");
        } catch (DriverNotFoundException | VehicleNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al asignar conductor: " + e.getMessage());
        }
        return "redirect:/vehicles";
    }

    @PostMapping("/reassign/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String reassignDriver(
            @PathVariable Long assignmentId,
            @RequestParam Long newDriverId,
            RedirectAttributes redirectAttributes) {
        try {
            assignmentService.reassignDriver(assignmentId, newDriverId);
            redirectAttributes.addFlashAttribute("successMessage", "Conductor reasignado exitosamente");
        } catch (AssignmentNotFoundException | DriverNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vehicles";
    }

    @GetMapping("/end/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String endAssignment(
            @PathVariable Long assignmentId,
            RedirectAttributes redirectAttributes) {
        try {
            assignmentService.endAssignment(assignmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Asignación finalizada exitosamente");
        } catch (AssignmentNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/vehicles";
    }
}