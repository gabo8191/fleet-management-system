package com.translogistics.fleetmanagmentsystem.model;

import com.translogistics.fleetmanagmentsystem.enums.IncidentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime incidentDate;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private IncidentType type;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
