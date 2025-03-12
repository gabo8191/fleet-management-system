package com.translogistics.fleetmanagmentsystem.service;


import com.translogistics.fleetmanagmentsystem.exceptions.AssignmentNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.DriverVehicleAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverVehicleAssignmentServiceTests {

    @Mock
    private DriverVehicleAssignmentRepository assignmentRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverVehicleAssignmentService assignmentService;

    private Driver testDriver;
    private Driver testNewDriver;
    private DriverVehicleAssignment testAssignment;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        // Configuración de un conductor de prueba
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setLicenseNumber("ABC123");

        // Configuración de un nuevo conductor para reasignación
        testNewDriver = new Driver();
        testNewDriver.setId(2L);
        testNewDriver.setLicenseNumber("XYZ789");

        // Configuración de una asignación de prueba
        testAssignment = new DriverVehicleAssignment();
        testAssignment.setId(100L);
        testAssignment.setDriver(testDriver);

        // Rango de fechas de ejemplo
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
    }

    @Test
    void isDriverAvailableShouldReturnTrueWhenDriverExistsAndNoAssignmentOverlapping() {
        // Simula que el conductor existe
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        // Simula que no hay asignación que se superponga en el rango dado
        when(assignmentRepository.existsByDriverAndAssignmentStartBetween(testDriver, start, end))
                .thenReturn(false);

        boolean available = assignmentService.isDriverAvailable(1L, start, end);

        assertTrue(available);
        verify(driverRepository).findById(1L);
        verify(assignmentRepository).existsByDriverAndAssignmentStartBetween(testDriver, start, end);
    }

    @Test
    void isDriverAvailableShouldReturnFalseWhenAssignmentExistsInRange() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        // Simula que existe una asignación en el rango indicado
        when(assignmentRepository.existsByDriverAndAssignmentStartBetween(testDriver, start, end))
                .thenReturn(true);

        boolean available = assignmentService.isDriverAvailable(1L, start, end);

        assertFalse(available);
        verify(driverRepository).findById(1L);
        verify(assignmentRepository).existsByDriverAndAssignmentStartBetween(testDriver, start, end);
    }

    @Test
    void isDriverAvailableShouldThrowExceptionWhenDriverNotFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DriverNotFoundException.class, () ->
                assignmentService.isDriverAvailable(1L, start, end)
        );

        assertEquals("Conductor no encontrado", exception.getMessage());
        verify(driverRepository).findById(1L);
        verify(assignmentRepository, never()).existsByDriverAndAssignmentStartBetween(any(), any(), any());
    }

    @Test
    void reassignDriverShouldUpdateAssignmentWithNewDriver() {
        // Simula que se encuentra la asignación y el nuevo conductor
        when(assignmentRepository.findById(100L)).thenReturn(Optional.of(testAssignment));
        when(driverRepository.findById(2L)).thenReturn(Optional.of(testNewDriver));
        when(assignmentRepository.save(any(DriverVehicleAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assignmentService.reassignDriver(100L, 2L);

        // Verifica que la asignación ahora tenga al nuevo conductor asignado
        assertEquals(testNewDriver, testAssignment.getDriver());
        verify(assignmentRepository).findById(100L);
        verify(driverRepository).findById(2L);
        verify(assignmentRepository).save(testAssignment);
    }

    @Test
    void reassignDriverShouldThrowExceptionWhenAssignmentNotFound() {
        when(assignmentRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AssignmentNotFoundException.class, () ->
                assignmentService.reassignDriver(100L, 2L)
        );

        assertEquals("Asignación no encontrada", exception.getMessage());
        verify(assignmentRepository).findById(100L);
        verify(driverRepository, never()).findById(anyLong());
        verify(assignmentRepository, never()).save(any(DriverVehicleAssignment.class));
    }

    @Test
    void reassignDriverShouldThrowExceptionWhenNewDriverNotFound() {
        // Simula que se encuentra la asignación
        when(assignmentRepository.findById(100L)).thenReturn(Optional.of(testAssignment));
        // Simula que el nuevo conductor no existe
        when(driverRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DriverNotFoundException.class, () ->
                assignmentService.reassignDriver(100L, 2L)
        );

        assertEquals("Conductor no encontrado", exception.getMessage());
        verify(assignmentRepository).findById(100L);
        verify(driverRepository).findById(2L);
        verify(assignmentRepository, never()).save(any(DriverVehicleAssignment.class));
    }
}
