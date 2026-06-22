package ariel.springframework.springbootvehiclesapp.repositories;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VehicleRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired
    VehicleRepository vehicleRepository;

    // Valid vehicle. Each test starts from this and overrides exactly one field
    // a failure points at one specific rule rather than a tangle of them
    private Vehicle.VehicleBuilder vehicleBuilder() {
        return Vehicle.builder()
                .vin("2TP55U913HJ7SPD44")
                .make(Make.TOYOTA)
                .model("Corolla")
                .modelYear(2024)
                .color("Silver")
                .mileage(15000)
                .price(new BigDecimal("24999.99"));
    }

    @Test
    void testSaveVehicle() {
        Vehicle saved = vehicleRepository.saveAndFlush(vehicleBuilder().build());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
    }

    @Test
    void testGetVehicleById() {
        Vehicle saved = vehicleRepository.saveAndFlush(vehicleBuilder().build());

        Optional<Vehicle> fetched = vehicleRepository.findById(saved.getId());

        assertThat(fetched).isPresent();
        assertThat(fetched.get().getVin()).isEqualTo(saved.getVin());
        assertThat(fetched.get().getModel()).isEqualTo("Corolla");
    }

    @Test
    void testVinIsRequiredDatabaseConstraint() {
        Vehicle invalid = vehicleBuilder().vin(null).build();

        assertThatThrownBy(() -> vehicleRepository.saveAndFlush(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testVinMustBeUnique() {
        vehicleRepository.saveAndFlush(vehicleBuilder().build());

        Vehicle duplicate = vehicleBuilder().build();

        assertThatThrownBy(() -> vehicleRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testFindAllByModel() {
        vehicleRepository.saveAndFlush(vehicleBuilder().model("TEST_MODEL_A").vin("TESTVIN0000000001").build());
        vehicleRepository.saveAndFlush(vehicleBuilder().model("TEST_MODEL_A").vin("TESTVIN0000000002").build());

        Page<Vehicle> page = vehicleRepository.findAllByModel("TEST_MODEL_A", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allMatch(v -> v.getModel().equals("TEST_MODEL_A"));
    }

    @Test
    void testFindAllByMake() {
        vehicleRepository.saveAndFlush(vehicleBuilder().make(Make.TOYOTA).vin("TESTVIN0000000003").build());

        Page<Vehicle> page = vehicleRepository.findAllByMake(Make.TOYOTA, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).extracting(Vehicle::getMake).containsOnly(Make.TOYOTA);
    }

    @Test
    void testFindAllByModelAndMake() {
        vehicleRepository.saveAndFlush(
                vehicleBuilder().make(Make.TOYOTA).model("TEST_MODEL_B").vin("TESTVIN0000000004").build());

        Page<Vehicle> page = vehicleRepository.findAllByModelAndMake("TEST_MODEL_B", Make.TOYOTA, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getVin()).isEqualTo("TESTVIN0000000004");
    }
}
