package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
