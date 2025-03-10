package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.model.Trip;
import com.translogistics.fleetmanagmentsystem.enums.TripStatus;
import com.translogistics.fleetmanagmentsystem.model.Driver;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByDriverAndStatus(Driver driver, TripStatus status);
    List<Trip> findByVehicleAndStatus(Vehicle vehicle, TripStatus status);
    List<Trip> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Trip> findByOriginAndDestination(String origin, String destination);
}