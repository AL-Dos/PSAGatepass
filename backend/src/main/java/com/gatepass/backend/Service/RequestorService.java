package com.gatepass.backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.RequestorRepository;

@Service
public class RequestorService {
    private final RequestorRepository requestorRepository;
    
    private final ModelMapper modelMapper;

    RequestorService(RequestorRepository requestorRepository, ModelMapper modelMapper) {
        this.requestorRepository = requestorRepository;
        this.modelMapper = modelMapper;
    }
    
    public List<RequestorDTO> getAllRequestorsWithEquipmentAndGatepass() {
        List<Requestors> requestors = requestorRepository.findAll();
        
        return requestors.stream()
            .map(requestor -> modelMapper.map(requestor, RequestorDTO.class))
            .collect(Collectors.toList());
    }
}
