package com.gatepass.backend.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gatepass.backend.Data.EquipmentDTO;
import com.gatepass.backend.Model.Equipments;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Custom mapping for Equipments to EquipmentDTO
        // This maps the gatepass fields to the equipment DTO
        mapper.typeMap(Equipments.class, EquipmentDTO.class)
            .addMappings(m -> {
                m.map(Equipments::getId, EquipmentDTO::setId);
                m.map(src -> src.getGatepass().isReleased(), EquipmentDTO::setReleased);
                m.map(src -> src.getGatepass().isReturned(), EquipmentDTO::setReturned);
                m.map(src -> src.getGatepass().getReleasedAt(), EquipmentDTO::setReleasedAt);
                m.map(src -> src.getGatepass().getReturnedAt(), EquipmentDTO::setReturnedAt);
            });
        
        return mapper;
    }
}
