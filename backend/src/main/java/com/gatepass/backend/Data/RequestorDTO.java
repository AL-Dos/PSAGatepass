package com.gatepass.backend.Data;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestorDTO {
    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Destination is required")
    public String destination;

    @NotBlank(message = "Period is required")
    public String period;

    @NotBlank(message = "Purpose is required")
    public String purpose;

    @NotNull(message = "Equipment is required")
    @NotEmpty(message = "Equipment list cannot be empty")
    @Valid
    public List<EquipmentDTO> equipment;
}
