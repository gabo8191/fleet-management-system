package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTests {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setLicensePlate("ABC123");
        testVehicle.setModel("Test Model");
        testVehicle.setYear(2020);
        testVehicle.setType(VehicleType.CARGA);
        testVehicle.setStatus(VehicleStatus.ACTIVO);

        // Campos heredados de BaseEntity
        testVehicle.setCreatedAt(LocalDateTime.now());
        testVehicle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void saveOrUpdateShouldCallRepositorySave() {
        // Configurar el mock
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        Vehicle result = vehicleService.saveOrUpdate(testVehicle);

        // Assert
        verify(vehicleRepository, times(1)).save(testVehicle);
        assertNotNull(result);
        assertEquals("Test Model", result.getModel());
    }

    @Test
    void getListShouldReturnAllVehicles() {
        // Given
        List<Vehicle> vehicles = Arrays.asList(testVehicle);
        when(vehicleRepository.findAll()).thenReturn(vehicles);

        // Act
        List<Vehicle> result = vehicleService.getList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Model", result.get(0).getModel());
        assertNotNull(result.get(0).getCreatedAt()); // Verificar campos de auditoría
    }

    @Test
    void getByIdShouldReturnVehicleWhenFound() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<Vehicle> result = vehicleService.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ABC123", result.get().getLicensePlate());
        assertNotNull(result.get().getUpdatedAt());
    }

    @Test
    void getByIdShouldReturnEmptyWhenNotFound() {
        // Given
        when(vehicleRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<Vehicle> result = vehicleService.getById(2L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteByIdShouldCallRepositoryDeleteById() {
        // Act
        vehicleService.deleteById(1L);

        // Assert
        verify(vehicleRepository, times(1)).deleteById(1L);
    }
}