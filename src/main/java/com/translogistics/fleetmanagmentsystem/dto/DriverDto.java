package com.translogistics.fleetmanagmentsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DriverDto {

    private Long id;


    private Long userId;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String licenseNumber;

    @Min(value = 0, message = "Los años de experiencia deben ser un número positivo")
    private Integer experienceYears;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}