package com.translogistics.fleetmanagmentsystem.dto;

import com.translogistics.fleetmanagmentsystem.enums.MaintenanceType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class MaintenanceDto {

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private MaintenanceType type;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    @NotNull(message = "El costo es obligatorio")
    private Double cost;

    @NotBlank(message = "El proveedor es obligatorio")
    private String provider;

    // Getters y Setters
    public MaintenanceType getType() {
        return type;
    }

    public void setType(MaintenanceType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}