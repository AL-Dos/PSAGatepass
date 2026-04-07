package com.gatepass.backend.Data;

import java.util.List;

import lombok.Data;

@Data
public class RequestorDTO {
    public String name;
    public String destination;
    public String period;
    public String purpose;
    public List<EquipmentDTO> equipment; 
}
