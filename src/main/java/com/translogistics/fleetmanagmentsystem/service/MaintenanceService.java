package com.translogistics.fleetmanagmentsystem.service;

import com.translogistics.fleetmanagmentsystem.dto.MaintenanceDto;
import com.translogistics.fleetmanagmentsystem.enums.MaintenanceType;
import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.exceptions.VehicleNotFoundException;
import com.translogistics.fleetmanagmentsystem.model.Maintenance;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.MaintenanceRepository;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Maintenance logMaintenance(Long vehicleId, MaintenanceDto dto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado"));

        Maintenance maintenance = new Maintenance();
        maintenance.setType(dto.getType());
        maintenance.setDate(dto.getDate());
        maintenance.setDescription(dto.getDescription());
        maintenance.setCost(dto.getCost());
        maintenance.setProvider(dto.getProvider());
        maintenance.setVehicle(vehicle);

        // Actualizar estado del vehículo si es mantenimiento correctivo
        if (dto.getType() == MaintenanceType.CORRECTIVO) {
            vehicle.setStatus(VehicleStatus.MANTEMINIENTO);
            vehicleRepository.save(vehicle);
        }

        return maintenanceRepository.save(maintenance);
    }

    public List<Maintenance> findMaintenancesByVehicleId(Long vehicleId) {
        return maintenanceRepository.findByVehicleIdOrderByDateDesc(vehicleId);
    }

    public List<Maintenance> findUpcomingPreventiveMaintenance() {
        return maintenanceRepository.findByTypeAndDateGreaterThanEqual(
                MaintenanceType.PREVENTIVO,
                LocalDate.now()
        );
    }
}