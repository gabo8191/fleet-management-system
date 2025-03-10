package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.model.Maintenance;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.enums.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByVehicle(Vehicle vehicle);
    List<Maintenance> findByType(MaintenanceType type);
    List<Maintenance> findByDateBetween(LocalDate start, LocalDate end);
    List<Maintenance> findByVehicleAndDateBetween(Vehicle vehicle, LocalDate start, LocalDate end);
}