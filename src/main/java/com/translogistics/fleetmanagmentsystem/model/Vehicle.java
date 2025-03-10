package com.translogistics.fleetmanagmentsystem.model;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.ACTIVO;

    @OneToMany(mappedBy = "vehicle")
    private List<DriverVehicleAssignment> driverAssignments;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "vehicle")
    private List<Trip> trips;

    @OneToMany(mappedBy = "vehicle")
    private List<Maintenance> maintenances;

}
