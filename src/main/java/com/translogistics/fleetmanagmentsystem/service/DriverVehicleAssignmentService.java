package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.exceptions.AssignmentNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.DriverVehicleAssignmentRepository;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverVehicleAssignmentService {

    private final DriverVehicleAssignmentRepository assignmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public DriverVehicleAssignmentService(
            DriverVehicleAssignmentRepository assignmentRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository) {
        this.assignmentRepository = assignmentRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public DriverVehicleAssignment assignDriver(Long vehicleId, Long driverId, LocalDateTime assignmentStart) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        if (!isDriverAvailable(driverId, assignmentStart, assignmentStart.plusHours(1))) {
            throw new IllegalStateException("El conductor ya tiene una asignación en este horario");
        }

        DriverVehicleAssignment assignment = new DriverVehicleAssignment();
        assignment.setVehicle(vehicle);
        assignment.setDriver(driver);
        assignment.setAssignmentStart(assignmentStart);
        assignment.setActive(true);

        return assignmentRepository.save(assignment);
    }

    public boolean isDriverAvailable(Long driverId, LocalDateTime start, LocalDateTime end) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        return !assignmentRepository.existsByDriverAndAssignmentStartBetween(driver, start, end);
    }

    public void reassignDriver(Long assignmentId, Long newDriverId) {
        DriverVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada"));

        Driver newDriver = driverRepository.findById(newDriverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        assignment.setDriver(newDriver);
        assignmentRepository.save(assignment);
    }

    public void endAssignment(Long assignmentId) {
        DriverVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada"));

        assignment.setActive(false);
        assignment.setAssignmentEnd(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }

    public List<DriverVehicleAssignment> findAssignmentsByDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        return assignmentRepository.findByDriverAndIsActiveTrue(driver);
    }

    public List<DriverVehicleAssignment> findAssignmentsByVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        return assignmentRepository.findByVehicleAndIsActiveTrue(vehicle);
    }
}