package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.exceptions.AssignmentNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.DriverVehicleAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DriverVehicleAssignmentService {
    private final DriverVehicleAssignmentRepository assignmentRepository;
    private final DriverRepository driverRepository;

    public DriverVehicleAssignmentService(DriverVehicleAssignmentRepository assignmentRepository, DriverRepository driverRepository) {
        this.assignmentRepository = assignmentRepository;
        this.driverRepository = driverRepository;
    }

    // Método de validación de disponibilidad
    public boolean isDriverAvailable(Long driverId, LocalDateTime start, LocalDateTime end) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        return !assignmentRepository.existsByDriverAndAssignmentStartBetween(driver, start, end);
    }

    // Método de reasignación
    public void reassignDriver(Long assignmentId, Long newDriverId) {
        DriverVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada"));

        Driver newDriver = driverRepository.findById(newDriverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        assignment.setDriver(newDriver);
        assignmentRepository.save(assignment);
    }
}
