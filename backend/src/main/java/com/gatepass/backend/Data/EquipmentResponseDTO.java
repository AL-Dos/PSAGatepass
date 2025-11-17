package com.gatepass.backend.Data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EquipmentResponseDTO {
    private Long id;
    private String equipmentName;
    private int quantity;
    private String equipmentCode;
}
