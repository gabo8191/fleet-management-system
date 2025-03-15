package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public boolean licensePlateExists(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }

    public Vehicle createVehicle(VehicleDto vehicleDto) {
        Vehicle vehicle = new Vehicle();
        mapDtoToEntity(vehicleDto, vehicle);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        mapDtoToEntity(vehicleDto, vehicle);
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    private void mapDtoToEntity(VehicleDto dto, Vehicle entity) {
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setModel(dto.getModel());
        entity.setYear(dto.getYear());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
    }

    public Vehicle toggleVehicleStatus(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        VehicleStatus newStatus = switch (vehicle.getStatus()) {
            case ACTIVO -> VehicleStatus.FUERA_DE_SERVICIO;
            case FUERA_DE_SERVICIO -> VehicleStatus.ACTIVO;
            default -> vehicle.getStatus();
        };

        vehicle.setStatus(newStatus);
        return vehicleRepository.save(vehicle);
    }

    // Removed duplicate assignDriver method - now only in DriverVehicleAssignmentService
}