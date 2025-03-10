package com.translogistics.fleetmanagmentsystem.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "drivers")
public class Driver extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private int experienceYears;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "driver")
    private List<DriverVehicleAssignment> vehicleAssignments;

    @OneToMany(mappedBy = "driver")
    private List<Trip> trips;

    @OneToMany(mappedBy = "driver")
    private List<Vehicle> vehicles;




}
