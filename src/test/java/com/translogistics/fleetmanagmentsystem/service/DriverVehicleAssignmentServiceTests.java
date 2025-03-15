package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.DriverVehicleAssignmentRepository;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverVehicleAssignmentServiceTests {

    @Mock
    private DriverVehicleAssignmentRepository assignmentRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DriverVehicleAssignmentService assignmentService;

    @Test
    void testAssignDriverSuccess() {
        // Given
        Long vehicleId = 1L;
        Long driverId = 2L;
        LocalDateTime assignmentStart = LocalDateTime.now();

        Driver driver = new Driver();
        driver.setId(driverId);
        // Set additional driver fields if needed

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus(VehicleStatus.ACTIVO); // Vehicle must be active

        // Stub repository calls
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        // When checking existing assignments, return an empty list so the driver and vehicle are available
        when(assignmentRepository.findByDriverAndIsActiveTrue(any(Driver.class)))
                .thenReturn(Collections.emptyList());
        when(assignmentRepository.findByVehicleAndIsActiveTrue(any(Vehicle.class)))
                .thenReturn(Collections.emptyList());
        // Stub the save call to return the assignment that was passed in
        when(assignmentRepository.save(any(DriverVehicleAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DriverVehicleAssignment assignment = assignmentService.assignDriver(vehicleId, driverId, assignmentStart);

        // Then
        assertNotNull(assignment);
        assertEquals(driver, assignment.getDriver(), "The assignment should have the expected driver");
        assertEquals(vehicle, assignment.getVehicle(), "The assignment should have the expected vehicle");
        assertEquals(assignmentStart, assignment.getAssignmentStart(), "The assignment start time should match");
        assertTrue(assignment.isActive(), "The assignment should be active");

        // Verify that the vehicle status was updated to ASIGNADO_A_VIAJE
        assertEquals(VehicleStatus.ASIGNADO_A_VIAJE, vehicle.getStatus(), "Vehicle status should be updated to ASIGNADO_A_VIAJE");

        // Verify repository interactions
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(driverRepository, times(1)).findById(driverId);
        verify(assignmentRepository, times(1)).save(any(DriverVehicleAssignment.class));
    }

    @Test
    void testAssignDriverDriverNotFound() {
        // Given
        Long vehicleId = 1L;
        Long driverId = 2L;
        LocalDateTime assignmentStart = LocalDateTime.now();

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setStatus(VehicleStatus.ACTIVO);

        // Stub repository calls so driver is not found
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(DriverNotFoundException.class, () ->
                assignmentService.assignDriver(vehicleId, driverId, assignmentStart));

        verify(driverRepository, times(1)).findById(driverId);
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(assignmentRepository, never()).save(any(DriverVehicleAssignment.class));
    }

    @Test
    void testAssignDriverVehicleNotFound() {
        // Given
        Long vehicleId = 1L;
        Long driverId = 2L;
        LocalDateTime assignmentStart = LocalDateTime.now();

        Driver driver = new Driver();
        driver.setId(driverId);

        // Stub repository calls so vehicle is not found
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        // Although these assignmentRepository calls won't be reached, stub them to avoid NPEs
        when(assignmentRepository.findByDriverAndIsActiveTrue(any(Driver.class)))
                .thenReturn(Collections.emptyList());
        when(assignmentRepository.findByVehicleAndIsActiveTrue(any(Vehicle.class)))
                .thenReturn(Collections.emptyList());

        // When/Then
        assertThrows(VehicleNotFoundException.class, () ->
                assignmentService.assignDriver(vehicleId, driverId, assignmentStart));

        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(driverRepository, never()).findById(driverId); // Actually, vehicle lookup is done first
        verify(assignmentRepository, never()).save(any(DriverVehicleAssignment.class));
    }
}
