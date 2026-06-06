package ariel.springframework.springbootvehiclesapp.controller;

import ariel.springframework.springbootvehiclesapp.controllers.VehicleController;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import ariel.springframework.springbootvehiclesapp.services.VehicleService;
import ariel.springframework.springbootvehiclesapp.services.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebMvcTest
public class VehicleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    VehicleService vehicleService;

    VehicleServiceImpl vehicleServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<VehicleDTO> vehicleArgumentCaptor;

    @BeforeEach
    void setUp() {
        vehicleServiceImpl = new VehicleServiceImpl();
    }

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwtBuilder -> {
                jwtBuilder.claims(claimMap -> {
                            claimMap.put("scope", "message-read");
                            claimMap.put("scope", "message-write");
                        })
                        .subject("message-client")
                        .notBefore(Instant.now().minusSeconds(51));
            });

    @Test
    void testCreateNewVehicle() throws Exception {
        VehicleDTO vehicleToPost = vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false)
                .getContent()
                .getFirst();

        VehicleDTO savedVehicle = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .vin(vehicleToPost.getVin())
                .make(vehicleToPost.getMake())
                .model(vehicleToPost.getModel())
                .modelYear(vehicleToPost.getModelYear())
                .color(vehicleToPost.getColor())
                .mileage(vehicleToPost.getMileage())
                .price(vehicleToPost.getPrice())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        given(vehicleService.saveNewVehicle(any(VehicleDTO.class)))
                .willReturn(savedVehicle);

        mockMvc.perform(post(VehicleController.VEHICLE_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleToPost)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void testListVehicles() throws Exception {
        given(vehicleService.listVehicles(any(), any(), any(), any(), any(), any()))
                .willReturn(vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false));

        mockMvc.perform(get(VehicleController.VEHICLE_PATH)
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(1)));
    }

    @Test
    void testGetVehicleById() throws Exception {
        VehicleDTO vehicle = vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false).getContent().getFirst();

        given(vehicleService.getVehicleById(any(UUID.class))).willReturn(Optional.of(vehicle));

        mockMvc.perform(get(VehicleController.VEHICLE_PATH_ID, vehicle.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(vehicle.getId().toString())))
                .andExpect(jsonPath("$.make", is(vehicle.getMake().toString())))
                .andExpect(jsonPath("$.model", is(vehicle.getModel())));
    }

    @Test
    void testUpdateVehicle() throws Exception {
        VehicleDTO vehicle = vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false).getContent().getFirst();

        given(vehicleService.updateVehicleById(any(), any())).willReturn(Optional.of(vehicle));

        mockMvc.perform(put(VehicleController.VEHICLE_PATH_ID, vehicle.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isNoContent());

        verify(vehicleService).updateVehicleById(any(UUID.class), any(VehicleDTO.class));
    }

    @Test
    void testPatchVehicle() throws Exception {
        VehicleDTO vehicle = vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false).getContent().getFirst();

        Map<String, Object> vehicleMap = new HashMap<>();
        vehicleMap.put("model", "New Model");

        given(vehicleService.patchVehicleById(any(UUID.class), any(VehicleDTO.class)))
                .willReturn(Optional.of(vehicle));

        mockMvc.perform(patch(VehicleController.VEHICLE_PATH_ID, vehicle.getId())
                .with(jwtRequestPostProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleMap)))
                .andExpect(status().isNoContent());

        verify(vehicleService).patchVehicleById(uuidArgumentCaptor.capture(), vehicleArgumentCaptor.capture());

        assertThat(vehicle.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(vehicleMap.get("model")).isEqualTo(vehicleArgumentCaptor.getValue().getModel());
    }

    @Test
    void testDeleteVehicle() throws Exception {
        VehicleDTO vehicle = vehicleServiceImpl.listVehicles(null, null, null, 1, 25, false).getContent().getFirst();

        given(vehicleService.deleteById(any())).willReturn(true);

        mockMvc.perform(delete(VehicleController.VEHICLE_PATH_ID, vehicle.getId())
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vehicleService).deleteById(uuidArgumentCaptor.capture());
        assertThat(vehicle.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }
}
