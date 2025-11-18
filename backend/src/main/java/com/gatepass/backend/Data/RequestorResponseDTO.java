package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestorResponseDTO {
    private Long id;
    private String name;
    private String destination;
    private String period;
    private List<EquipmentResponseDTO> equipmentItems;

    private String notedBy;
    private String approvedBy;
    private String returnedCheck;
}
