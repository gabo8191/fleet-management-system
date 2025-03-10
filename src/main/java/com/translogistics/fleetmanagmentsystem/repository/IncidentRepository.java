package com.translogistics.fleetmanagmentsystem.repository;

import com.translogistics.fleetmanagmentsystem.model.Incident;
import com.translogistics.fleetmanagmentsystem.model.Trip;
import com.translogistics.fleetmanagmentsystem.enums.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByTrip(Trip trip);
    List<Incident> findByType(IncidentType type);
    List<Incident> findByIncidentDateBetween(LocalDateTime start, LocalDateTime end);
}