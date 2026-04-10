package com.gatepass.backend.Data;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentDTO {
    private Long id;

    @NotBlank(message = "Equipment name is required")
    public String equipmentName;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;

    @NotBlank(message = "Equipment code is required")
    public String equipmentCode;

    private boolean released;
    private boolean returned;
    private OffsetDateTime releasedAt;
    private OffsetDateTime returnedAt;
}
