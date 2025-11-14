// package com.gatepass.backend.Service;

// import java.util.List;

// import org.springframework.stereotype.Service;

// import com.gatepass.backend.Data.GatepassRequestDTO;
// import com.gatepass.backend.Model.Equipments;
// import com.gatepass.backend.Model.Gatepass;
// import com.gatepass.backend.Model.GatepassItems;
// import com.gatepass.backend.Model.Requestors;
// import com.gatepass.backend.Repository.EquipmentRepository;
// import com.gatepass.backend.Repository.GatepassRepository;
// import com.gatepass.backend.Repository.RequestorRepository;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class GatepassService {
//     private final GatepassRepository gatepassRepository;
//     private final RequestorRepository requestorRepository;
//     private final EquipmentRepository equipmentRepository;

//     @Transactional
//     public Gatepass createGatepass(GatepassRequestDTO dto) {
//         Requestors req = requestorRepository.findById(dto.getRequestorId())
//                 .orElseThrow(() -> new RuntimeException("Requestor not found"));

//         Gatepass gatepass = new Gatepass();
//         gatepass.setRequestor(req);

//         List<GatepassItems> items = dto.getItems().stream().map(i -> {
//             Equipments eq = equipmentRepository.findById(i.getEquipmentId())
//                     .orElseThrow(() -> new RuntimeException("Equipment not found"));
//             GatepassItem item = new GatepassItem();
//             item.setEquipment(eq);
//             item.setQuantity(i.getQuantity());
//             item.setGatepass(gatepass);
//             return item;
//         }).toList();

//         gatepass.setItems(items);
//         return gatepassRepository.save(gatepass);
//     }
// }