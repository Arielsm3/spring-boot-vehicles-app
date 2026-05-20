package ariel.springframework.springbootvehiclesapp.services;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface VehicleService {

    VehicleDTO saveNewVehicle(VehicleDTO vehicle);

    Page<VehicleDTO> listVehicles(String vin, Make make, String model, Integer pageNumber, Integer pageSize, Boolean showMileage);

    Optional<VehicleDTO> getVehicleById(UUID id);

    Optional<VehicleDTO> updateVehicleById(UUID id, VehicleDTO vehicle);

    Boolean deleteById(UUID id);

    Optional<VehicleDTO> patchVehicleById(UUID id, VehicleDTO vehicle);
}
