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
}
