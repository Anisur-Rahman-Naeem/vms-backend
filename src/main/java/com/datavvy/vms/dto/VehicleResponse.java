package com.datavvy.vms.dto;

import com.datavvy.vms.entity.Vehicle;

import java.time.LocalDate;

public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String company;
    private String type;
    private String brand;
    private String model;
    private String status;
    private String vin;
    private Integer year;
    private String fuelType;
    private Integer mileage;
    private String assignedDriver;
    private String department;
    private LocalDate lastServiceDate;
    private String notes;

    public VehicleResponse() {}

    public VehicleResponse(Long id, String vehicleNumber, String company, String type, String brand, String model, String status) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.company = company;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.status = status;
    }

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleResponse r = new VehicleResponse();
        r.id = vehicle.getId();
        r.vehicleNumber = vehicle.getVehicleNumber();
        r.company = vehicle.getCompany();
        r.type = vehicle.getType();
        r.brand = vehicle.getBrand();
        r.model = vehicle.getModel();
        r.status = vehicle.getStatus();
        r.vin = vehicle.getVin();
        r.year = vehicle.getYear();
        r.fuelType = vehicle.getFuelType();
        r.mileage = vehicle.getMileage();
        r.assignedDriver = vehicle.getAssignedDriver();
        r.department = vehicle.getDepartment();
        r.lastServiceDate = vehicle.getLastServiceDate();
        r.notes = vehicle.getNotes();
        return r;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public String getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(String assignedDriver) { this.assignedDriver = assignedDriver; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
