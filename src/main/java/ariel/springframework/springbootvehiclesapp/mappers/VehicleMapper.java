package ariel.springframework.springbootvehiclesapp.mappers;

import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import org.mapstruct.Mapper;

@Mapper
public interface VehicleMapper {

    Vehicle vehicleDtoToVehicle(VehicleDTO dto);

    VehicleDTO vehicleToVehicleDto(Vehicle vehicle);
}
