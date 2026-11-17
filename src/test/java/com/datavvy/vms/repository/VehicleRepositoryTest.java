package com.datavvy.vms.repository;

import com.datavvy.vms.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    @DisplayName("Vehicle entity persists and maps correctly to SQL")
    void testVehicleEntityMapping() {
        Vehicle vehicle = new Vehicle(
                "DHK-GA-55-1234",
                "Metro Express",
                "Sedan",
                "Toyota",
                "Camry",
                "ACTIVE"
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(saved.getId(), "Generated ID should not be null");

        Optional<Vehicle> found = vehicleRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("DHK-GA-55-1234", found.get().getVehicleNumber());
        assertEquals("Metro Express", found.get().getCompany());
        assertEquals("Sedan", found.get().getType());
        assertEquals("Toyota", found.get().getBrand());
        assertEquals("Camry", found.get().getModel());
        assertEquals("ACTIVE", found.get().getStatus());
    }

    @Test
    @DisplayName("SQL unique constraint on vehicle_number rejects duplicates at database level")
    void testVehicleNumberUniqueConstraint() {
        Vehicle vehicle1 = new Vehicle(
                "UNIQUE-101",
                "Company A",
                "Van",
                "Ford",
                "Transit",
                "ACTIVE"
        );
        vehicleRepository.saveAndFlush(vehicle1);

        Vehicle vehicle2 = new Vehicle(
                "UNIQUE-101",
                "Company B",
                "SUV",
                "Honda",
                "CR-V",
                "ACTIVE"
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> vehicleRepository.saveAndFlush(vehicle2),
                "Database must reject duplicate vehicle_number with DataIntegrityViolationException"
        );
    }

    @Test
    @DisplayName("existsByVehicleNumber returns true for existing and false for non-existing")
    void testExistsByVehicleNumber() {
        Vehicle vehicle = new Vehicle(
                "EXISTS-007",
                "Transport Corp",
                "Bus",
                "Scania",
                "K410",
                "ACTIVE"
        );
        vehicleRepository.saveAndFlush(vehicle);

        assertTrue(vehicleRepository.existsByVehicleNumber("EXISTS-007"));
        assertFalse(vehicleRepository.existsByVehicleNumber("DOES-NOT-EXIST"));
    }
}

