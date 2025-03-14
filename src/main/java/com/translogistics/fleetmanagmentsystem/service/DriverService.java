package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.DriverDto;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> findAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver findById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));
    }

    public Driver createDriver(DriverDto driverDto) {
        if (licenseNumberExists(driverDto.getLicenseNumber())) {
            throw new IllegalArgumentException("El número de licencia ya está registrado");
        }

        Driver driver = new Driver();
        mapDtoToEntity(driverDto, driver);
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, DriverDto driverDto) {
        Driver driver = findById(id);

        if (!driver.getLicenseNumber().equals(driverDto.getLicenseNumber())
                && licenseNumberExists(driverDto.getLicenseNumber())) {
            throw new IllegalArgumentException("El número de licencia ya está en uso");
        }

        mapDtoToEntity(driverDto, driver);
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        Driver driver = findById(id);
        driverRepository.delete(driver);
    }

    public boolean licenseNumberExists(String licenseNumber) {
        return driverRepository.findByLicenseNumber(licenseNumber).isPresent();
    }

    private void mapDtoToEntity(DriverDto dto, Driver entity) {
        entity.setLicenseNumber(dto.getLicenseNumber());
        entity.setExperienceYears(dto.getExperienceYears());
    }
}