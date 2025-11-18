package com.gatepass.backend.Util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gatepass.backend.Data.EquipmentResponseDTO;
import com.gatepass.backend.Data.RequestorResponseDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Requestors;

@Component
public class RequestorMapper {
    public RequestorResponseDTO toDto(Requestors entity) {
        if (entity == null) return null;
        RequestorResponseDTO dto = new RequestorResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDestination(entity.getDestination());
        dto.setPeriod(entity.getPeriod());

        List<EquipmentResponseDTO> items = entity.getEquipment() == null ? List.of() : entity.getEquipment().stream().map(this::toEquipmentDto).collect(Collectors.toList());
        dto.setEquipmentItems(items);

        dto.setNotedBy(entity.getNotedBy());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setReturnedCheck(entity.getReturnedCheck());
        return dto;
    }

    private EquipmentResponseDTO toEquipmentDto(Equipments equipment) {
        EquipmentResponseDTO equipDTO = new EquipmentResponseDTO();
        equipDTO.setId(equipment.getId());
        equipDTO.setEquipmentName(equipment.getEquipmentName());
        equipDTO.setQuantity(equipment.getQuantity());
        equipDTO.setEquipmentCode(equipment.getEquipmentCode());
        return equipDTO;
    }

    public List<RequestorResponseDTO> toDtoList(List<Requestors> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    } 
}
