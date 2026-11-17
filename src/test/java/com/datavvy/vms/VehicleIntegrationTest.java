package com.datavvy.vms;

import com.datavvy.vms.dto.VehicleRequest;
import com.datavvy.vms.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:vms_integration_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void cleanUp() {
        vehicleRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete CRUD lifecycle works independently without frontend")
    void testCompleteVehicleCrudLifecycle() throws Exception {
        // 1. CREATE (POST)
        VehicleRequest createRequest = new VehicleRequest(
                "INT-TEST-001",
                "Speedy Transit",
                "Van",
                "Mercedes",
                "Sprinter",
                "ACTIVE"
        );

        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.vehicleNumber").value("INT-TEST-001"))
                .andExpect(jsonPath("$.company").value("Speedy Transit"))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readTree(responseJson).get("id").asLong();

        // 2. READ ALL (GET)
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(createdId));

        // 3. READ BY ID (GET /{id})
        mockMvc.perform(get("/api/vehicles/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId))
                .andExpect(jsonPath("$.vehicleNumber").value("INT-TEST-001"));

        // 4. DUPLICATE CHECK (POST with same number)
        VehicleRequest duplicateRequest = new VehicleRequest(
                "INT-TEST-001",
                "Another Company",
                "Truck",
                "MAN",
                "TGX",
                "ACTIVE"
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));

        // 5. UPDATE (PUT /{id})
        VehicleRequest updateRequest = new VehicleRequest(
                "INT-TEST-001",
                "Speedy Transit International",
                "Van",
                "Mercedes",
                "Sprinter 2024",
                "MAINTENANCE"
        );

        mockMvc.perform(put("/api/vehicles/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Speedy Transit International"))
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));

        // 6. DELETE (DELETE /{id})
        mockMvc.perform(delete("/api/vehicles/{id}", createdId))
                .andExpect(status().isNoContent());

        // 7. VERIFY DELETED (GET /{id} -> 404)
        mockMvc.perform(get("/api/vehicles/{id}", createdId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertFalse(vehicleRepository.existsById(createdId));
    }
}

