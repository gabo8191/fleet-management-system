package com.translogistics.fleetmanagmentsystem.model;

import com.translogistics.fleetmanagmentsystem.enums.VehicleStatus;
import com.translogistics.fleetmanagmentsystem.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vehicles")

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public List<DriverVehicleAssignment> getDriverAssignments() {
        return driverAssignments;
    }

    public void setDriverAssignments(List<DriverVehicleAssignment> driverAssignments) {
        this.driverAssignments = driverAssignments;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }
}
