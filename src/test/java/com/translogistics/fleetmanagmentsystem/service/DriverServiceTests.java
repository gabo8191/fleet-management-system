package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver testDriver;
    private DriverDto testDriverDto;

    @BeforeEach
    void setUp() {
        // Configuración de un conductor de prueba
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setLicenseNumber("ABC123");
        testDriver.setExperienceYears(5);

        // Configuración de un DTO para crear/actualizar conductor
        testDriverDto = new DriverDto();
        testDriverDto.setLicenseNumber("XYZ789");
        testDriverDto.setExperienceYears(7);
    }

    @Test
    void findAllDriversShouldReturnAllDrivers() {
        List<Driver> drivers = Arrays.asList(testDriver);
        when(driverRepository.findAll()).thenReturn(drivers);

        List<Driver> result = driverService.findAllDrivers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(driverRepository).findAll();
    }

    @Test
    void findByIdShouldReturnDriverWhenExists() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));

        Driver result = driverService.findById(1L);

        assertNotNull(result);
        assertEquals("ABC123", result.getLicenseNumber());
        verify(driverRepository).findById(1L);
    }

    @Test
    void findByIdShouldThrowExceptionWhenDriverNotFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DriverNotFoundException.class, () ->
                driverService.findById(1L)
        );

        assertEquals("Conductor no encontrado", exception.getMessage());
    }

    @Test
    void createDriverShouldCreateDriverWhenLicenseDoesNotExist() {
        // Simula que el número de licencia no está registrado
        when(driverRepository.findByLicenseNumber(testDriverDto.getLicenseNumber())).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver savedDriver = invocation.getArgument(0);
            savedDriver.setId(2L); // Asignamos un ID simulando la persistencia
            return savedDriver;
        });

        Driver result = driverService.createDriver(testDriverDto);

        assertNotNull(result);
        assertEquals("XYZ789", result.getLicenseNumber());
        assertEquals(7, result.getExperienceYears());
        verify(driverRepository).findByLicenseNumber(testDriverDto.getLicenseNumber());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void createDriverShouldThrowExceptionWhenLicenseExists() {
        // Simula que el número de licencia ya existe
        when(driverRepository.findByLicenseNumber(testDriverDto.getLicenseNumber())).thenReturn(Optional.of(testDriver));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                driverService.createDriver(testDriverDto)
        );

        assertEquals("El número de licencia ya está registrado", exception.getMessage());
    }

    @Test
    void updateDriverShouldUpdateDriverWhenNoLicenseConflict() {
        // Se modifica el número de licencia del conductor original para que no coincida con el del DTO
        testDriver.setLicenseNumber("OLD123");
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(driverRepository.findByLicenseNumber(testDriverDto.getLicenseNumber())).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Driver result = driverService.updateDriver(1L, testDriverDto);

        assertNotNull(result);
        assertEquals("XYZ789", result.getLicenseNumber());
        assertEquals(7, result.getExperienceYears());
        verify(driverRepository).findById(1L);
        verify(driverRepository).findByLicenseNumber(testDriverDto.getLicenseNumber());
        verify(driverRepository).save(testDriver);
    }

    @Test
    void updateDriverShouldThrowExceptionWhenLicenseConflict() {
        // El número de licencia del conductor original es diferente y ya está en uso por otro conductor
        testDriver.setLicenseNumber("OLD123");
        Driver anotherDriver = new Driver();
        anotherDriver.setId(2L);
        anotherDriver.setLicenseNumber(testDriverDto.getLicenseNumber());

        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(driverRepository.findByLicenseNumber(testDriverDto.getLicenseNumber())).thenReturn(Optional.of(anotherDriver));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                driverService.updateDriver(1L, testDriverDto)
        );

        assertEquals("El número de licencia ya está en uso", exception.getMessage());
    }

    @Test
    void deleteDriverShouldDeleteDriverWhenFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        doNothing().when(driverRepository).delete(testDriver);

        driverService.deleteDriver(1L);

        verify(driverRepository).findById(1L);
        verify(driverRepository).delete(testDriver);
    }
}
