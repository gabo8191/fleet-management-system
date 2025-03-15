package com.translogistics.fleetmanagmentsystem.controller;

import com.translogistics.fleetmanagmentsystem.exceptions.AssignmentNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.service.DriverService;
import com.translogistics.fleetmanagmentsystem.service.DriverVehicleAssignmentService;
import com.translogistics.fleetmanagmentsystem.service.UserService;
import com.translogistics.fleetmanagmentsystem.service.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final UserService userService;

    public DriverVehicleAssignmentController(
            DriverVehicleAssignmentService assignmentService,
            DriverService driverService,
            VehicleService vehicleService, UserService userService) {
        this.assignmentService = assignmentService;
        this.driverService = driverService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String listAssignments(Model model) {
        model.addAttribute("assignments", assignmentService.findAll());
        model.addAttribute("drivers", driverService.findAllDrivers());
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        return "assignment/list-assignments";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String showAssignmentForm(Model model) {
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        model.addAttribute("drivers", driverService.findAllDrivers());
        model.addAttribute("users", userService.findAllUsers());
        return "assignment/assignDriver";
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String assignDriver(
            @RequestParam Long vehicleId,
            @RequestParam Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime assignmentStart,
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

    @GetMapping("/details/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String viewVehicleAssignments(@PathVariable Long vehicleId, Model model) {
        try {
            model.addAttribute("assignments", assignmentService.findAssignmentsByVehicle(vehicleId));
            model.addAttribute("vehicle", vehicleService.findById(vehicleId).orElseThrow(() ->
                    new VehicleNotFoundException("Vehículo no encontrado")));
            model.addAttribute("drivers", driverService.findAllDrivers());
            return "assignments/vehicle-assignments";
        } catch (VehicleNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
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
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al reasignar conductor: " + e.getMessage());
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

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String viewDriverAssignments(@PathVariable Long driverId, Model model) {
        try {
            model.addAttribute("assignments", assignmentService.findAssignmentsByDriver(driverId));
            model.addAttribute("driver", driverService.findById(driverId));
            return "assignments/driver-assignments";
        } catch (DriverNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @GetMapping("/edit/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public String showEditAssignmentForm(@PathVariable Long assignmentId, Model model) {
        try {
            var assignment = assignmentService.findById(assignmentId);

            model.addAttribute("assignment", assignment);
            model.addAttribute("drivers", driverService.findAllDrivers());

            return "assignment/edit-assignment";
        } catch (AssignmentNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }
}