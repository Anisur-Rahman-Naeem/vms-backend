package com.datavvy.vms.service;

import com.datavvy.vms.dto.VehicleRequest;
import com.datavvy.vms.dto.VehicleResponse;
import com.datavvy.vms.entity.Vehicle;
import com.datavvy.vms.exception.DuplicateVehicleNumberException;
import com.datavvy.vms.repository.VehicleRepository;
import com.datavvy.vms.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new VehicleRequest(
                "DHK-METRO-GA-11-2233",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );
    }

    @Test
    @DisplayName("createVehicle succeeds when vehicleNumber is unique")
    void createVehicle_success_whenVehicleNumberIsUnique() {
        when(vehicleRepository.existsByVehicleNumber("DHK-METRO-GA-11-2233")).thenReturn(false);

        Vehicle savedVehicle = new Vehicle(
                1L,
                "DHK-METRO-GA-11-2233",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        VehicleResponse response = vehicleService.createVehicle(sampleRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("DHK-METRO-GA-11-2233", response.getVehicleNumber());
        assertEquals("Apex Logistics", response.getCompany());
        assertEquals("Truck", response.getType());
        assertEquals("Volvo", response.getBrand());
        assertEquals("FH16", response.getModel());
        assertEquals("ACTIVE", response.getStatus());

        verify(vehicleRepository).existsByVehicleNumber("DHK-METRO-GA-11-2233");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("createVehicle throws DuplicateVehicleNumberException when vehicleNumber already exists")
    void createVehicle_throwsDuplicateVehicleNumberException_whenNumberAlreadyExists() {
        when(vehicleRepository.existsByVehicleNumber("DHK-METRO-GA-11-2233")).thenReturn(true);

        DuplicateVehicleNumberException exception = assertThrows(
                DuplicateVehicleNumberException.class,
                () -> vehicleService.createVehicle(sampleRequest)
        );

        assertEquals("Vehicle with number 'DHK-METRO-GA-11-2233' already exists", exception.getMessage());
        assertEquals("DHK-METRO-GA-11-2233", exception.getVehicleNumber());

        verify(vehicleRepository).existsByVehicleNumber("DHK-METRO-GA-11-2233");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("updateVehicle throws DuplicateVehicleNumberException when new number belongs to another vehicle")
    void updateVehicle_throwsDuplicateVehicleNumberException_whenNewNumberBelongsToAnother() {
        Vehicle existingVehicle = new Vehicle(
                1L,
                "OLD-NUM-001",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.existsByVehicleNumber("DHK-METRO-GA-11-2233")).thenReturn(true);

        DuplicateVehicleNumberException exception = assertThrows(
                DuplicateVehicleNumberException.class,
                () -> vehicleService.updateVehicle(1L, sampleRequest)
        );

        assertEquals("DHK-METRO-GA-11-2233", exception.getVehicleNumber());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("updateVehicle succeeds when keeping the same vehicle number")
    void updateVehicle_succeeds_whenKeepingSameVehicleNumber() {
        Vehicle existingVehicle = new Vehicle(
                1L,
                "DHK-METRO-GA-11-2233",
                "Old Company",
                "Truck",
                "Volvo",
                "FH16",
                "MAINTENANCE"
        );
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.updateVehicle(1L, sampleRequest);

        assertNotNull(response);
        assertEquals("DHK-METRO-GA-11-2233", response.getVehicleNumber());
        assertEquals("Apex Logistics", response.getCompany());
        verify(vehicleRepository, never()).existsByVehicleNumber(any());
        verify(vehicleRepository).save(existingVehicle);
    }
}

