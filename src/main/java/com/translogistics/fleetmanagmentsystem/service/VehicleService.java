package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.VehicleDto;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import com.translogistics.fleetmanagmentsystem.exceptions.DriverNotFoundException;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.DriverRepository;
import com.translogistics.fleetmanagmentsystem.repository.DriverVehicleAssignmentRepository;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final DriverVehicleAssignmentRepository assignmentRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository,
            DriverVehicleAssignmentRepository assignmentRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.assignmentRepository = assignmentRepository;
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
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        VehicleStatus newStatus = switch (vehicle.getStatus()) {
            case ACTIVO -> VehicleStatus.FUERA_DE_SERVICIO;
            case FUERA_DE_SERVICIO -> VehicleStatus.ACTIVO;
            default -> vehicle.getStatus();
        };

        vehicle.setStatus(newStatus);
        return vehicleRepository.save(vehicle);
    }

    public void assignDriver(Long vehicleId, Long driverId, LocalDateTime assignmentStart) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado"));

        LocalDate date = assignmentStart.toLocalDate();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        if (assignmentRepository.existsByDriverAndAssignmentStartBetween(driver, startOfDay, endOfDay)) {
            throw new IllegalStateException("El conductor ya tiene una asignación en esta fecha");
        }

        DriverVehicleAssignment assignment = new DriverVehicleAssignment();
        assignment.setVehicle(vehicle);
        assignment.setDriver(driver);
        assignment.setAssignmentStart(assignmentStart);
        assignmentRepository.save(assignment);

        vehicle.setStatus(VehicleStatus.ASIGNADO_A_VIAJE);
        vehicleRepository.save(vehicle);
    }
}