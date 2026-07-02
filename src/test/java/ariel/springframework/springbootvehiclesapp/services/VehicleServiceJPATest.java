package ariel.springframework.springbootvehiclesapp.services;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.mappers.VehicleMapper;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import ariel.springframework.springbootvehiclesapp.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceJPATest {

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    VehicleMapper vehicleMapper;

    @InjectMocks
    VehicleServiceJPA vehicleServiceJPA;

    private Vehicle buildValidVehicle() {
        return Vehicle.builder()
                .id(UUID.randomUUID())
                .vin("1HGCM82633A004352")
                .make(Make.TOYOTA)
                .model("Corolla")
                .modelYear(2024)
                .color("Silver")
                .mileage(15000)
                .price(new BigDecimal("24999.99"))
                .build();
    }

    private VehicleDTO buildValidVehicleDTO() {
        return VehicleDTO.builder()
                .id(UUID.randomUUID())
                .vin("1HGCM82633A004352")
                .make(Make.TOYOTA)
                .model("Corolla")
                .modelYear(2024)
                .color("Silver")
                .mileage(15000)
                .price(new BigDecimal("24999.99"))
                .build();
    }

    @Test
    void buildPageRequest_defaultsWhenNull() {
        PageRequest result = vehicleServiceJPA.buildPageRequest(null, null);

        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
    }

    @Test
    void buildPageRequest_convertsOneBasedToZeroBased() {
        PageRequest result = vehicleServiceJPA.buildPageRequest(1, 25);

        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(25);
    }

    @Test
    void buildPageRequest_pageNumberZeroFallsToDefault() {
        PageRequest result = vehicleServiceJPA.buildPageRequest(0, 25);

        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageNumber()).isEqualTo(0);
    }

    @Test
    void buildPageRequest_capsSizeAtMax() {
        PageRequest result = vehicleServiceJPA.buildPageRequest(1, 5000);

        assertThat(result.getPageSize()).isEqualTo(1000);
    }

    @Test
    void listVehicles_byModelOnly_callsFindAllByModel() {
        given(vehicleRepository.findAllByModel(any(), any())).willReturn(Page.empty());

        vehicleServiceJPA.listVehicles(null, null, "Corolla", 1, 25, false);

        verify(vehicleRepository).findAllByModel(eq("Corolla"), any(Pageable.class));
        verify(vehicleRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void listVehicles_byMakeOnly_callsFindAllByMake() {
        given(vehicleRepository.findAllByMake(any(), any())).willReturn(Page.empty());

        vehicleServiceJPA.listVehicles(null, Make.TOYOTA, null, 1, 25, false);

        verify(vehicleRepository).findAllByMake(eq(Make.TOYOTA), any(Pageable.class));
        verify(vehicleRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void listVehicles_byModelAndMake_callsFindAllByModelAndMake() {
        given(vehicleRepository.findAllByModelAndMake(any(), any(), any())).willReturn(Page.empty());

        vehicleServiceJPA.listVehicles(null, Make.TOYOTA, "Corolla", 1, 25, false);

        verify(vehicleRepository).findAllByModelAndMake(eq("Corolla"), eq(Make.TOYOTA), any(Pageable.class));
        verify(vehicleRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void listVehicles_callsFindAll() {
        given(vehicleRepository.findAll(any(Pageable.class))).willReturn(Page.empty());

        vehicleServiceJPA.listVehicles(null, null, null, 1, 25, false);

        verify(vehicleRepository).findAll(any(Pageable.class));
        verify(vehicleRepository, never()).findAllByModelAndMake(any(), any(), any());
    }

    @Test
    void deleteById_whenExists_deletesAndReturnsTrue() {
        given(vehicleRepository.existsById(any(UUID.class))).willReturn(true);

        Boolean result = vehicleServiceJPA.deleteById(UUID.randomUUID());

        assertThat(result).isTrue();
        verify(vehicleRepository).deleteById(any(UUID.class));
    }

    @Test
    void deleteById_whenNotExists_returnsFalseAndDoesNotDelete() {
        given(vehicleRepository.existsById(any(UUID.class))).willReturn(false);

        Boolean result = vehicleServiceJPA.deleteById(UUID.randomUUID());

        assertThat(result).isFalse();
        verify(vehicleRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void getVehicleById_whenFound_returnsMappedDto() {
        Vehicle entity = buildValidVehicle();
        VehicleDTO dto = buildValidVehicleDTO();

        given(vehicleRepository.findById(any(UUID.class))).willReturn(Optional.of(entity));
        given(vehicleMapper.vehicleToVehicleDto(entity)).willReturn(dto);

        Optional<VehicleDTO> result = vehicleServiceJPA.getVehicleById(UUID.randomUUID());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(dto);
    }

    @Test
    void getVehicleById_whenNotFound_returnsEmpty() {
        given(vehicleRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        Optional<VehicleDTO> result = vehicleServiceJPA.getVehicleById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void patchVehicleById_updatesOnlyNonNullFields() {
        Vehicle existing = buildValidVehicle();
        String originalColor = existing.getColor();
        String originalVin = existing.getVin();

        VehicleDTO patch = VehicleDTO.builder()
                .model("Camry")
                .build();

        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);

        given(vehicleRepository.findById(any(UUID.class))).willReturn(Optional.of(existing));
        given(vehicleRepository.save(any(Vehicle.class))).willReturn(existing);
        given(vehicleMapper.vehicleToVehicleDto(any(Vehicle.class))).willReturn(buildValidVehicleDTO());

        vehicleServiceJPA.patchVehicleById(UUID.randomUUID(), patch);

        verify(vehicleRepository).save(vehicleCaptor.capture());
        Vehicle saved = vehicleCaptor.getValue();

        assertThat(saved.getModel()).isEqualTo("Camry");
        assertThat(saved.getColor()).isEqualTo(originalColor);
        assertThat(saved.getVin()).isEqualTo(originalVin);
    }

    @Test
    void patchVehicleById_whenNotFound_returnsEmptyAndDoesNotSave() {
        VehicleDTO dto = buildValidVehicleDTO();

        given(vehicleRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        Optional<VehicleDTO> result = vehicleServiceJPA.patchVehicleById(UUID.randomUUID(), dto);

        assertThat(result).isEmpty();

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}
