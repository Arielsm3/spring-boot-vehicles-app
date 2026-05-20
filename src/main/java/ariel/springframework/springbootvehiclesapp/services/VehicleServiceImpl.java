package ariel.springframework.springbootvehiclesapp.services;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

    private Map<UUID, VehicleDTO> vehicleMap;

    public VehicleServiceImpl() {
        this.vehicleMap = new HashMap<>();

        VehicleDTO vehicle1 = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .vin("12345678910111213")
                .version(1)
                .make(Make.NISSAN)
                .model("Pathfinder")
                .year(Year.of(2024))
                .color("Gray")
                .mileage(21989)
                .price(new BigDecimal("9899.99"))
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        vehicleMap.put(vehicle1.getId(), vehicle1);
    }

    @Override
    public VehicleDTO saveNewVehicle(VehicleDTO vehicle) {
        VehicleDTO savedVehicle = VehicleDTO.builder()
                .id(UUID.randomUUID())
                .vin(vehicle.getVin())
                .version(vehicle.getVersion())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .mileage(vehicle.getMileage())
                .price(vehicle.getPrice())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        vehicleMap.put(savedVehicle.getId(), savedVehicle);

        return savedVehicle;
    }

    @Override
    public Page<VehicleDTO> listVehicles(String vin, Make make, String model, Integer pageNumber, Integer pageSize, Boolean showMileage) {
        return new PageImpl<>(new ArrayList<>(vehicleMap.values()));
    }

    @Override
    public Optional<VehicleDTO> getVehicleById(UUID id) {
        log.debug("Get vehicle ID method in service layer called.");

        return Optional.ofNullable(vehicleMap.get(id));
    }

    @Override
    public Optional<VehicleDTO> updateVehicleById(UUID id, VehicleDTO vehicle) {
        VehicleDTO existing = vehicleMap.get(id);
        if(existing == null) return Optional.empty();

        existing.setVin(vehicle.getVin());
        existing.setVersion(vehicle.getVersion());
        existing.setMake(vehicle.getMake());
        existing.setModel(vehicle.getModel());
        existing.setYear(vehicle.getYear());
        existing.setColor(vehicle.getColor());
        existing.setMileage(vehicle.getMileage());
        existing.setPrice(vehicle.getPrice());

        vehicleMap.put(existing.getId(), existing);
        return Optional.of(existing);
    }

    @Override
    public Optional<VehicleDTO> patchVehicleById(UUID id, VehicleDTO vehicle) {
        VehicleDTO existing = vehicleMap.get(id);
        if(existing == null) return Optional.empty();

        if(StringUtils.hasText(vehicle.getVin())) {
            existing.setVin(vehicle.getVin());
        }
        if(vehicle.getVersion() != null) {
            existing.setVersion(vehicle.getVersion());
        }
        if(vehicle.getMake() != null) {
            existing.setMake(vehicle.getMake());
        }
        if(StringUtils.hasText(vehicle.getModel())) {
            existing.setModel(vehicle.getModel());
        }
        if(vehicle.getYear() != null) {
            existing.setYear(vehicle.getYear());
        }
        if(StringUtils.hasText(vehicle.getColor())) {
            existing.setColor(vehicle.getColor());
        }
        if(vehicle.getMileage() != null) {
            existing.setMileage(vehicle.getMileage());
        }
        if(vehicle.getPrice() != null) {
            existing.setPrice(vehicle.getPrice());
        }

        vehicleMap.put(existing.getId(), existing);
        return Optional.of(existing);
    }

    @Override
    public Boolean deleteById(UUID id) {
        return vehicleMap.remove(id) != null;
    }


}
