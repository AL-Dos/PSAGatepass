// package com.gatepass.backend.Service;

// import java.util.List;

// import org.springframework.stereotype.Service;

// import com.gatepass.backend.Data.EquipmentResponseDTO;
// import com.gatepass.backend.Data.GatepassResponseDTO;
// import com.gatepass.backend.Data.RequestorResponseDTO;
// import com.gatepass.backend.Model.Requestors;
// import com.gatepass.backend.Repository.GatepassRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class GatepassQueryService {
//     private final GatepassRepository gatepassRepository;

//         public List<GatepassResponseDTO> getAllGatepasses() {
//         return gatepassRepository.findAll().stream().map(g -> {
//             Requestors r = g.getRequestor();
//             List<EquipmentResponseDTO> eqDtos = g.getItems().stream()
//                     .map(i -> new EquipmentResponseDTO(
//                             i.getEquipment().getId(),
//                             i.getEquipment().getEquipment(),
//                             i.getEquipment().getNumber(),
//                             i.getQuantity()))
//                     .toList();
//             return new GatepassResponseDTO(
//                     g.getId(),
//                     g.getDateIssued(),
//                     new RequestorResponseDTO(r.getId(), r.getRequestor(), r.getDestinaton(), r.getPeriod()),
//                     eqDtos
//             );
//         }).toList();
//     }
// }
