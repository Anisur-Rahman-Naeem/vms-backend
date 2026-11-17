package com.datavvy.vms.service.impl;

import com.datavvy.vms.dto.VehicleRequest;
import com.datavvy.vms.dto.VehicleResponse;
import com.datavvy.vms.entity.Vehicle;
import com.datavvy.vms.exception.DuplicateVehicleNumberException;
import com.datavvy.vms.exception.VehicleNotFoundException;
import com.datavvy.vms.repository.VehicleRepository;
import com.datavvy.vms.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        String normalizedNumber = normalizeVehicleNumber(request.getVehicleNumber());

        if (vehicleRepository.existsByVehicleNumber(normalizedNumber)) {
            throw new DuplicateVehicleNumberException(normalizedNumber);
        }

        Vehicle vehicle = mapToEntity(new Vehicle(), request, normalizedNumber);
        return VehicleResponse.fromEntity(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        return VehicleResponse.fromEntity(
                vehicleRepository.findById(id)
                        .orElseThrow(() -> new VehicleNotFoundException(id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByNumber(String vehicleNumber) {
        String normalizedNumber = normalizeVehicleNumber(vehicleNumber);
        return VehicleResponse.fromEntity(
                vehicleRepository.findByVehicleNumber(normalizedNumber)
                        .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with number: " + normalizedNumber))
        );
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        String normalizedNumber = normalizeVehicleNumber(request.getVehicleNumber());

        if (!existing.getVehicleNumber().equalsIgnoreCase(normalizedNumber)
                && vehicleRepository.existsByVehicleNumber(normalizedNumber)) {
            throw new DuplicateVehicleNumberException(normalizedNumber);
        }

        mapToEntity(existing, request, normalizedNumber);
        return VehicleResponse.fromEntity(vehicleRepository.save(existing));
    }

    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException(id);
        }
        vehicleRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private Vehicle mapToEntity(Vehicle vehicle, VehicleRequest request, String normalizedNumber) {
        vehicle.setVehicleNumber(normalizedNumber);

        String company = trimOrNull(request.getCompany());
        if (company == null || company.isEmpty()) {
            company = trimOrNull(request.getDepartment());
            if (company == null || company.isEmpty()) {
                company = "Datavvy";
            }
        }
        vehicle.setCompany(company);

        vehicle.setType(request.getType().trim());
        vehicle.setBrand(request.getBrand().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setStatus(request.getStatus().trim());
        vehicle.setVin(trimOrNull(request.getVin()));
        vehicle.setYear(request.getYear());
        vehicle.setFuelType(trimOrNull(request.getFuelType()));
        vehicle.setMileage(request.getMileage());
        vehicle.setAssignedDriver(trimOrNull(request.getAssignedDriver()));
        vehicle.setDepartment(trimOrNull(request.getDepartment()));
        vehicle.setLastServiceDate(request.getLastServiceDate());
        vehicle.setNotes(trimOrNull(request.getNotes()));
        return vehicle;
    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty");
        }
        return vehicleNumber.trim().toUpperCase();
    }
}
