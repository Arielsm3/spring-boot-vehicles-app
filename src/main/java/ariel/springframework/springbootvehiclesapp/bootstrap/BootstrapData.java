package ariel.springframework.springbootvehiclesapp.bootstrap;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import ariel.springframework.springbootvehiclesapp.entities.Vehicle;
import ariel.springframework.springbootvehiclesapp.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BootstrapData implements ApplicationRunner {

    private final VehicleRepository vehicleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadVehicleData();
    }

    private void loadVehicleData() {
        if(vehicleRepository.count() == 0) {
            Vehicle vehicle1 = Vehicle.builder()
                    .vin("1HGCM82633A123456")
                    .make(Make.HONDA)
                    .model("Accord")
                    .modelYear(2023)
                    .color("Silver")
                    .mileage(15000)
                    .price(new BigDecimal("24999.99"))
                    .build();

            Vehicle vehicle2 = Vehicle.builder()
                    .vin("2T1BURHE0JC123456")
                    .make(Make.TOYOTA)
                    .model("Corolla")
                    .modelYear(2022)
                    .color("Blue")
                    .mileage(22000)
                    .price(new BigDecimal("21500.00"))
                    .build();

            Vehicle vehicle3 = Vehicle.builder()
                    .vin("1FA6P8TH5J5123456")
                    .make(Make.FORD)
                    .model("Mustang")
                    .modelYear(2024)
                    .color("Red")
                    .mileage(5000)
                    .price(new BigDecimal("42000.00"))
                    .build();

            Vehicle vehicle4 = Vehicle.builder()
                    .vin("3WWM98IO44J123456")
                    .make(Make.LAMBORGHINI)
                    .model("Murcielago")
                    .modelYear(2008)
                    .color("Black")
                    .mileage(85000)
                    .price(new BigDecimal("185000.00"))
                    .build();

            Vehicle vehicle5 = Vehicle.builder()
                    .vin("55QRL690SHH123456")
                    .make(Make.BENTLEY)
                    .model("Continental GT")
                    .modelYear(2022)
                    .color("Gray")
                    .mileage(25000)
                    .price(new BigDecimal("65000.00"))
                    .build();

            Vehicle vehicle6 = Vehicle.builder()
                    .vin("78THSSJMBGW123456")
                    .make(Make.BMW)
                    .model("X5 M")
                    .modelYear(2010)
                    .color("White")
                    .mileage(32000)
                    .price(new BigDecimal("31000.00"))
                    .build();

            vehicleRepository.saveAll(java.util.List.of(
                    vehicle1, vehicle2, vehicle3, vehicle4, vehicle5, vehicle6
            ));
        }
    }
}
