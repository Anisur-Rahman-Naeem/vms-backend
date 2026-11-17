package com.datavvy.vms.exception;

public class DuplicateVehicleNumberException extends RuntimeException {

    private final String vehicleNumber;

    public DuplicateVehicleNumberException(String vehicleNumber) {
        super("Vehicle with number '" + vehicleNumber + "' already exists");
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}

