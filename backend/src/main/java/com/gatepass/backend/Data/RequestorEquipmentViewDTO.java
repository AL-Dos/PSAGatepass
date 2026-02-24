package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;

@Data
public class RequestorEquipmentViewDTO {
    private Long id;
    private String name;
    private String destination;
    private String period;
    private List<EquipmentDTO> equipment;
}
