package com.datavvy.vms.controller;

import com.datavvy.vms.dto.VehicleRequest;
import com.datavvy.vms.dto.VehicleResponse;
import com.datavvy.vms.exception.DuplicateVehicleNumberException;
import com.datavvy.vms.exception.GlobalExceptionHandler;
import com.datavvy.vms.exception.VehicleNotFoundException;
import com.datavvy.vms.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@Import(GlobalExceptionHandler.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private VehicleRequest validRequest;
    private VehicleResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = new VehicleRequest(
                "DHK-METRO-GA-11-2233",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );

        sampleResponse = new VehicleResponse(
                1L,
                "DHK-METRO-GA-11-2233",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );
    }

    @Test
    @DisplayName("POST /api/vehicles returns 201 Created when vehicle is created")
    void createVehicle_returns201_whenValid() throws Exception {
        when(vehicleService.createVehicle(any(VehicleRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehicleNumber").value("DHK-METRO-GA-11-2233"))
                .andExpect(jsonPath("$.company").value("Apex Logistics"))
                .andExpect(jsonPath("$.brand").value("Volvo"));
    }

    @Test
    @DisplayName("POST /api/vehicles returns 409 Conflict when vehicleNumber is duplicate")
    void createVehicle_returns409_whenDuplicateVehicleNumber() throws Exception {
        when(vehicleService.createVehicle(any(VehicleRequest.class)))
                .thenThrow(new DuplicateVehicleNumberException("DHK-METRO-GA-11-2233"));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.vehicleNumber").value("DHK-METRO-GA-11-2233"));
    }

    @Test
    @DisplayName("POST /api/vehicles returns 400 Bad Request when validation fails")
    void createVehicle_returns400_whenVehicleNumberIsBlank() throws Exception {
        VehicleRequest invalidRequest = new VehicleRequest(
                "",
                "Apex Logistics",
                "Truck",
                "Volvo",
                "FH16",
                "ACTIVE"
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.vehicleNumber").exists());
    }

    @Test
    @DisplayName("GET /api/vehicles returns 200 OK with list of vehicles")
    void getAllVehicles_returns200_withList() throws Exception {
        VehicleResponse second = new VehicleResponse(
                2L,
                "DHK-METRO-CHA-55-9988",
                "City Movers",
                "Van",
                "Ford",
                "Transit",
                "ACTIVE"
        );
        when(vehicleService.getAllVehicles()).thenReturn(List.of(sampleResponse, second));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].vehicleNumber").value("DHK-METRO-GA-11-2233"))
                .andExpect(jsonPath("$[1].vehicleNumber").value("DHK-METRO-CHA-55-9988"));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} returns 200 OK when vehicle exists")
    void getVehicleById_returns200_whenFound() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/vehicles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehicleNumber").value("DHK-METRO-GA-11-2233"));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} returns 404 Not Found when vehicle does not exist")
    void getVehicleById_returns404_whenNotFound() throws Exception {
        when(vehicleService.getVehicleById(99L)).thenThrow(new VehicleNotFoundException(99L));

        mockMvc.perform(get("/api/vehicles/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("GET /api/vehicles/by-number/{vehicleNumber} returns 200 OK when found")
    void getVehicleByNumber_returns200_whenFound() throws Exception {
        when(vehicleService.getVehicleByNumber("DHK-METRO-GA-11-2233")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/vehicles/by-number/{vehicleNumber}", "DHK-METRO-GA-11-2233"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vehicleNumber").value("DHK-METRO-GA-11-2233"));
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id} returns 200 OK when update succeeds")
    void updateVehicle_returns200_whenUpdated() throws Exception {
        VehicleResponse updatedResponse = new VehicleResponse(
                1L,
                "DHK-METRO-GA-11-2233",
                "Updated Logistics Corp",
                "Truck",
                "Volvo",
                "FH16",
                "MAINTENANCE"
        );
        when(vehicleService.updateVehicle(eq(1L), any(VehicleRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/vehicles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Updated Logistics Corp"))
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id} returns 404 Not Found when vehicle does not exist")
    void updateVehicle_returns404_whenNotFound() throws Exception {
        when(vehicleService.updateVehicle(eq(99L), any(VehicleRequest.class)))
                .thenThrow(new VehicleNotFoundException(99L));

        mockMvc.perform(put("/api/vehicles/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} returns 204 No Content when deleted")
    void deleteVehicle_returns204_whenDeleted() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/vehicles/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} returns 404 Not Found when vehicle does not exist")
    void deleteVehicle_returns404_whenNotFound() throws Exception {
        doThrow(new VehicleNotFoundException(99L)).when(vehicleService).deleteVehicle(99L);

        mockMvc.perform(delete("/api/vehicles/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
