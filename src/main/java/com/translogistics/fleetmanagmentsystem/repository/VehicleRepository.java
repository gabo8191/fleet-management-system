package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByStatus(VehicleStatus status);
}
