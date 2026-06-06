package ariel.springframework.springbootvehiclesapp.repository;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Page<Vehicle> findAllByModel(String model, Pageable pageable);

    Page<Vehicle> findAllByMake(Make make, Pageable pageable);

    Page<Vehicle> findAllByModelAndMake(String model, Make make, Pageable pageable);


}
