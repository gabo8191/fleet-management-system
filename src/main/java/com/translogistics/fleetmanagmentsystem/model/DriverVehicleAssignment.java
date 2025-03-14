package com.translogistics.fleetmanagmentsystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_vehicle_assignments")
public class DriverVehicleAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDateTime assignmentStart;

    @Column
    private LocalDateTime assignmentEnd;

    @Column(nullable = false)
    private boolean isActive = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getAssignmentStart() {
        return assignmentStart;
    }

    public void setAssignmentStart(LocalDateTime assignmentStart) {
        this.assignmentStart = assignmentStart;
    }

    public LocalDateTime getAssignmentEnd() {
        return assignmentEnd;
    }

    public void setAssignmentEnd(LocalDateTime assignmentEnd) {
        this.assignmentEnd = assignmentEnd;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
