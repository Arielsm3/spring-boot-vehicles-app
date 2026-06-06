package ariel.springframework.springbootvehiclesapp.models;

import ariel.springframework.springbootvehiclesapp.entities.Make;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    @JsonProperty("id")
    private UUID id;

    @NotBlank
    private String vin;

    private Integer version;

    private Make make;

    private String model;

    @Column(name = "model_year")
    private Integer modelYear;

    private String color;

    private Integer mileage;

    private BigDecimal price;

    private LocalDateTime createdDate;

    private LocalDateTime updateDate;
}
