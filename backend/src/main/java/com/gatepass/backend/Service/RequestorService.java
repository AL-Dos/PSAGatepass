package com.gatepass.backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.RequestorRepository;

@Service
public class RequestorService {
    @Autowired
    private RequestorRepository requestorRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public List<RequestorDTO> getAllRequestorsWithEquipmentAndGatepass() {
        List<Requestors> requestors = requestorRepository.findAll();
        
        return requestors.stream()
            .map(requestor -> modelMapper.map(requestor, RequestorDTO.class))
            .collect(Collectors.toList());
    }
}
