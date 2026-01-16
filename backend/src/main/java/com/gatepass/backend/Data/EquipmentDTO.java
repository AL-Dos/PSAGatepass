package com.gatepass.backend.Data;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EquipmentDTO {
    public String equipmentName;
    public int quantity;
    public String equipmentCode;

    private boolean released;
    private boolean returned;
    private LocalDateTime releasedAt;
    private LocalDateTime returnedAt;
}
