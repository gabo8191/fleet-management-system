package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.DriverVehicleAssignment;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverVehicleAssignmentRepository extends JpaRepository<DriverVehicleAssignment, Long> {

    boolean existsByDriverAndAssignmentStartBetween(
            Driver driver,
            LocalDateTime start,
            LocalDateTime end
    );

    List<DriverVehicleAssignment> findByDriverAndIsActiveTrue(Driver driver);
    List<DriverVehicleAssignment> findByVehicleAndIsActiveTrue(Vehicle vehicle);
}