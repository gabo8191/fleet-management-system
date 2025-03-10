package com.translogistics.fleetmanagmentsystem.service;
import com.translogistics.fleetmanagmentsystem.model.Vehicle;
import com.translogistics.fleetmanagmentsystem.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle saveOrUpdate(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getList(){
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getById(Long id){
        return vehicleRepository.findById(id);
    }

    public void deleteById(Long id){
        vehicleRepository.deleteById(id);
    }
}
