package ariel.springframework.springbootvehiclesapp.controllers;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.exceptions.NotFoundException;
import ariel.springframework.springbootvehiclesapp.models.VehicleDTO;
import ariel.springframework.springbootvehiclesapp.services.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VehicleController {

    public static final String VEHICLE_PATH = "/api/v1/vehicle";
    public static final String VEHICLE_PATH_ID = VEHICLE_PATH + "/{vehicleId}";

    private final VehicleService vehicleService;

    @PostMapping(VEHICLE_PATH) // Create method
    public ResponseEntity<VehicleDTO> postVehicle(@Validated @RequestBody VehicleDTO vehicle) {
        VehicleDTO savedVehicle = vehicleService.saveNewVehicle(vehicle);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", VEHICLE_PATH + "/" + savedVehicle.getId().toString());

        return new ResponseEntity<>(savedVehicle, headers, HttpStatus.CREATED);
    }

    @GetMapping(value = VEHICLE_PATH_ID)
    public VehicleDTO getVehicleById(@PathVariable("vehicleId")UUID id) {
        log.debug("Get vehicle by ID - in controller");

        return vehicleService.getVehicleById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping(VEHICLE_PATH)
    public Page<VehicleDTO> listVehicles(
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) Make make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Boolean showMileage
    ) {

        return vehicleService.listVehicles(vin, make, model, pageNumber, pageSize, showMileage);
    }

    @PutMapping(VEHICLE_PATH_ID)
    public ResponseEntity<VehicleDTO> updateVehicleById(@PathVariable("vehicleId") UUID id, @Validated @RequestBody VehicleDTO vehicle) {
        if(vehicleService.updateVehicleById(id, vehicle).isEmpty()) throw new NotFoundException();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(VEHICLE_PATH_ID)
    public ResponseEntity<VehicleDTO> patchVehicleById(@PathVariable("vehicleId") UUID id, @RequestBody VehicleDTO vehicle) {
        if(vehicleService.patchVehicleById(id, vehicle).isEmpty()) throw new NotFoundException();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(VEHICLE_PATH_ID)
    public ResponseEntity<VehicleDTO> deleteVehicleById(@PathVariable("vehicleId") UUID id) {
        if(!vehicleService.deleteById(id)) throw new NotFoundException();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
