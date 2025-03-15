package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
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
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Assigns a driver to a vehicle at the specified start time.
     * Updates the vehicle status to ASIGNADO_A_VIAJE.
     * Checks if the driver is available during the specified time.
     *
     * @param vehicleId        ID of the vehicle to assign
     * @param driverId         ID of the driver to assign
     * @param assignmentStart  Start time of the assignment
     * @return                 The created assignment
     * @throws VehicleNotFoundException  If vehicle is not found
     * @throws DriverNotFoundException   If driver is not found
     * @throws IllegalStateException     If driver is not available or vehicle is not in active status
     */


    @Transactional
    public DriverVehicleAssignment assignDriver(Long vehicleId, Long driverId, LocalDateTime assignmentStart) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        // Check if vehicle is in an active status
        if (vehicle.getStatus() != VehicleStatus.ACTIVO) {
            throw new IllegalStateException("El vehículo no está en estado activo para ser asignado");
        }

        // Check if driver is already assigned during this time (using consistent time window)
        // Using a standard assignment duration of 8 hours
        LocalDateTime assignmentEnd = assignmentStart.plusHours(8);
        if (!isDriverAvailable(driverId, assignmentStart, assignmentEnd)) {
            throw new IllegalStateException("El conductor ya tiene una asignación en este horario");
        }

        // Check if vehicle is already assigned during this time
        if (!isVehicleAvailable(vehicleId, assignmentStart, assignmentEnd)) {
            throw new IllegalStateException("El vehículo ya tiene una asignación en este horario");
        }

        // Create and save the assignment
        DriverVehicleAssignment assignment = new DriverVehicleAssignment();
        assignment.setVehicle(vehicle);
        assignment.setDriver(driver);
        assignment.setAssignmentStart(assignmentStart);
        assignment.setActive(true);

        // Update vehicle status and set the driver
        vehicle.setStatus(VehicleStatus.ASIGNADO_A_VIAJE);
        vehicle.setDriver(driver);  // Set the driver reference
        vehicleRepository.save(vehicle);

        return assignmentRepository.save(assignment);
    }

    /**
     * Checks if a driver is available during the specified time period.
     *
     * @param driverId  ID of the driver to check
     * @param start     Start time of the period
     * @param end       End time of the period
     * @return          true if the driver is available, false otherwise
     */
    public boolean isDriverAvailable(Long driverId, LocalDateTime start, LocalDateTime end) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        // Check if there are any active assignments for this driver that overlap with the specified period
        List<DriverVehicleAssignment> existingAssignments = assignmentRepository
                .findByDriverAndIsActiveTrue(driver);

        for (DriverVehicleAssignment assignment : existingAssignments) {
            // For active assignments without end time, assume a default 8-hour duration
            LocalDateTime assignmentEnd = assignment.getAssignmentEnd() != null
                    ? assignment.getAssignmentEnd()
                    : assignment.getAssignmentStart().plusHours(8);

            // Check for overlap
            if (!(end.isBefore(assignment.getAssignmentStart()) || start.isAfter(assignmentEnd))) {
                return false; // Overlap found
            }
        }

        return true; // No overlaps found
    }

    /**
     * Checks if a vehicle is available during the specified time period.
     *
     * @param vehicleId ID of the vehicle to check
     * @param start     Start time of the period
     * @param end       End time of the period
     * @return          true if the vehicle is available, false otherwise
     */
    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        // Check if there are any active assignments for this vehicle that overlap with the specified period
        List<DriverVehicleAssignment> existingAssignments = assignmentRepository
                .findByVehicleAndIsActiveTrue(vehicle);

        for (DriverVehicleAssignment assignment : existingAssignments) {
            // For active assignments without end time, assume a default 8-hour duration
            LocalDateTime assignmentEnd = assignment.getAssignmentEnd() != null
                    ? assignment.getAssignmentEnd()
                    : assignment.getAssignmentStart().plusHours(8);

            // Check for overlap
            if (!(end.isBefore(assignment.getAssignmentStart()) || start.isAfter(assignmentEnd))) {
                return false; // Overlap found
            }
        }

        return true; // No overlaps found
    }

    /**
     * Reassigns a vehicle to a new driver.
     *
     * @param assignmentId  ID of the assignment to modify
     * @param newDriverId   ID of the new driver
     * @throws AssignmentNotFoundException  If assignment is not found
     * @throws DriverNotFoundException      If driver is not found
     * @throws IllegalStateException        If new driver is not available
     */
    @Transactional
    public void reassignDriver(Long assignmentId, Long newDriverId) {
        DriverVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada"));

        Driver newDriver = driverRepository.findById(newDriverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        // Check if new driver is available for this assignment period
        LocalDateTime assignmentStart = assignment.getAssignmentStart();
        LocalDateTime assignmentEnd = assignment.getAssignmentEnd() != null
                ? assignment.getAssignmentEnd()
                : assignmentStart.plusHours(8);

        // Don't check for overlap with this particular assignment (since we're modifying it)
        if (!isDriverAvailableExcludingAssignment(newDriverId, assignmentStart, assignmentEnd, assignmentId)) {
            throw new IllegalStateException("El conductor nuevo ya tiene una asignación en este horario");
        }

        assignment.setDriver(newDriver);

        // Update the vehicle's driver reference
        Vehicle vehicle = assignment.getVehicle();
        vehicle.setDriver(newDriver);
        vehicleRepository.save(vehicle);

        assignmentRepository.save(assignment);
    }

    /**
     * Checks if a driver is available during a time period, excluding a specific assignment.
     */
    private boolean isDriverAvailableExcludingAssignment(
            Long driverId, LocalDateTime start, LocalDateTime end, Long excludedAssignmentId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        List<DriverVehicleAssignment> existingAssignments = assignmentRepository
                .findByDriverAndIsActiveTrue(driver);

        for (DriverVehicleAssignment assignment : existingAssignments) {
            // Skip the assignment we're modifying
            if (assignment.getId().equals(excludedAssignmentId)) {
                continue;
            }

            // For active assignments without end time, assume a default 8-hour duration
            LocalDateTime assignmentEnd = assignment.getAssignmentEnd() != null
                    ? assignment.getAssignmentEnd()
                    : assignment.getAssignmentStart().plusHours(8);

            // Check for overlap
            if (!(end.isBefore(assignment.getAssignmentStart()) || start.isAfter(assignmentEnd))) {
                return false; // Overlap found
            }
        }

        return true;
    }

    /**
     * Ends an active assignment and updates vehicle status.
     *
     * @param assignmentId  ID of the assignment to end
     * @throws AssignmentNotFoundException  If assignment is not found
     */
    @Transactional
    public void endAssignment(Long assignmentId) {
        DriverVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada"));

        // Update assignment status
        assignment.setActive(false);
        assignment.setAssignmentEnd(LocalDateTime.now());
        assignmentRepository.save(assignment);

        // Reset vehicle status to ACTIVO and remove driver reference
        Vehicle vehicle = assignment.getVehicle();
        vehicle.setStatus(VehicleStatus.ACTIVO);
        vehicle.setDriver(null);  // Clear the driver reference
        vehicleRepository.save(vehicle);
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