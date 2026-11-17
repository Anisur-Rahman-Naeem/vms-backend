package com.datavvy.vms.service;

import com.datavvy.vms.dto.VehicleRequest;
import com.datavvy.vms.dto.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    List<VehicleResponse> getAllVehicles();

    VehicleResponse getVehicleById(Long id);

    VehicleResponse getVehicleByNumber(String vehicleNumber);

    VehicleResponse updateVehicle(Long id, VehicleRequest request);

    void deleteVehicle(Long id);
}

