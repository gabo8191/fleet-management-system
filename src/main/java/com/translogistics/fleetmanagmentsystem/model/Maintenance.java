package com.translogistics.fleetmanagmentsystem.model;

import com.translogistics.fleetmanagmentsystem.enums.MaintenanceType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "maintenances")
public class Maintenance extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double cost;

    @Column(nullable = false)
    private String provider;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;


}
