package ariel.springframework.springbootvehiclesapp.services;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.mappers.VehicleMapper;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import ariel.springframework.springbootvehiclesapp.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class VehicleServiceJPA implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if(pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        }
        else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if(pageSize == null) {
            queryPageSize = DEFAULT_SIZE;
        }
        else {
            if(pageSize > 1000) {
                pageSize = 1000;
            }
            queryPageSize = pageSize;
        }

        Sort sort = Sort.by(Sort.Order.asc("model"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    @Override
    public VehicleDTO saveNewVehicle(VehicleDTO vehicle) {
        return vehicleMapper.vehicleToVehicleDto(vehicleRepository.save(vehicleMapper.vehicleDtoToVehicle(vehicle)));
    }

    @Override
    public Page<VehicleDTO> listVehicles(String vin, Make make, String model, Integer pageNumber, Integer pageSize, Boolean showMileage) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<Vehicle> vehiclePage;

        if(StringUtils.hasText(model) && make == null) vehiclePage = listVehiclesByModel(model, pageRequest);
        else if(!StringUtils.hasText(model) && make != null) vehiclePage = listVehiclesByMake(make, pageRequest);
        else if(StringUtils.hasText(model) && make != null) vehiclePage = listVehiclesByModelAndMake(model, make, pageRequest);
        else vehiclePage = vehicleRepository.findAll(pageRequest);

        return vehiclePage.map(vehicleMapper::vehicleToVehicleDto);
    }

    private Page<Vehicle> listVehiclesByModel(String model, Pageable pageable) {
        return vehicleRepository.findAllByModel(model, pageable);
    }

    private Page<Vehicle> listVehiclesByMake(Make make, Pageable pageable) {
        return vehicleRepository.findAllByMake(make, pageable);
    }

    private Page<Vehicle> listVehiclesByModelAndMake(String model, Make make, Pageable pageable) {
        return vehicleRepository.findAllByModelAndMake(model, make, pageable);
    };

    @Override
    public Optional<VehicleDTO> getVehicleById(UUID id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::vehicleToVehicleDto);
    }

    @Override
    public Optional<VehicleDTO> updateVehicleById(UUID id, VehicleDTO vehicle) {
        AtomicReference<Optional<VehicleDTO>> atomicReference = new AtomicReference<>();

        vehicleRepository.findById(id).ifPresentOrElse(foundVehicle -> {
            foundVehicle.setVin(vehicle.getVin());
            foundVehicle.setModel(vehicle.getModel());
            foundVehicle.setMake(vehicle.getMake());
            foundVehicle.setModelYear(vehicle.getModelYear());
            foundVehicle.setColor(vehicle.getColor());
            foundVehicle.setMileage(vehicle.getMileage());

            atomicReference.set(Optional.of(vehicleMapper
                    .vehicleToVehicleDto(vehicleRepository.save(foundVehicle))
            ));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }


    @Override
    public Optional<VehicleDTO> patchVehicleById(UUID id, VehicleDTO vehicle) {
        AtomicReference<Optional<VehicleDTO>> atomicReference = new AtomicReference<>();

        vehicleRepository.findById(id).ifPresentOrElse(foundVehicle -> {
            if(StringUtils.hasText(vehicle.getVin())) foundVehicle.setVin(vehicle.getVin());
            if(StringUtils.hasText(vehicle.getModel())) foundVehicle.setModel(vehicle.getModel());
            if(vehicle.getMake() != null) foundVehicle.setMake(vehicle.getMake());
            if(vehicle.getModelYear() != null) foundVehicle.setModelYear(vehicle.getModelYear());
            if(vehicle.getColor() != null) foundVehicle.setColor(vehicle.getColor());
            if(vehicle.getMileage() != null) foundVehicle.setMileage(vehicle.getMileage());

            atomicReference.set(Optional.of(vehicleMapper
                    .vehicleToVehicleDto(vehicleRepository.save(foundVehicle))
            ));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Boolean deleteById(UUID id) {
        if(vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
