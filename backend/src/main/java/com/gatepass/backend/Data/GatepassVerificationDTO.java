package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;

@Data
public class GatepassVerificationDTO {
    private boolean released;
    private boolean returned;
    private String requestor;
    private List<EquipmentDTO> equipment;
}