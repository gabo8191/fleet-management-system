package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.model.VehicleType;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import com.translogistics.fleetmanagmentsystem.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        testVehicle.setVehicleId(1L);
        testVehicle.setLicensePlate("ABC123");
        testVehicle.setModel("Test Model");
        testVehicle.setYear(2020);
        testVehicle.setType(VehicleType.CARGA);
    }

    @Test
    void saveOrUpdateShouldCallRepositorySave() {
        // Act
        vehicleService.saveOrUpdate(testVehicle);

        // Assert
        verify(vehicleRepository, times(1)).save(testVehicle);
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
    }

    @Test
    void getByIdShouldReturnVehicleWhenFound() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        Optional<Vehicle> result = vehicleService.getById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Model", result.get().getModel());
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
