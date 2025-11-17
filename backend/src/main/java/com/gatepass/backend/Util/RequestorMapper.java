package com.gatepass.backend.Util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gatepass.backend.Data.EquipmentResponseDTO;
import com.gatepass.backend.Data.RequestorResponseDTO;
import com.gatepass.backend.Model.Requestors;

@Component
public class RequestorMapper {
    public RequestorResponseDTO toDto(Requestors entity) {
        RequestorResponseDTO dto = new RequestorResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDestination(entity.getDestination());
        dto.setPeriod(entity.getPeriod());

        List<EquipmentResponseDTO> items = entity.getEquipment().stream().map(eq -> {
            EquipmentResponseDTO e = new EquipmentResponseDTO();
            e.setId(eq.getId());
            e.setEquipmentName(eq.getEquipmentName());
            e.setQuantity(eq.getQuantity());
            e.setEquipmentCode(eq.getEquipmentCode());
            return e;
        }).toList();

        dto.setEquipmentItems(items);
        return dto;
    }

    public List<RequestorResponseDTO> toDtoList(List<Requestors> entities) {
        return entities.stream().map(this::toDto).toList();
    } 
}
