package com.gatepass.backend.Data;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class EquipmentDTO {
    private Long id;
    public String equipmentName;
    public int quantity;
    public String equipmentCode;

    private boolean released;
    private boolean returned;
    private OffsetDateTime releasedAt;
    private OffsetDateTime returnedAt;
}
