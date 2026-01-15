package com.gatepass.backend.Controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.RequestorRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequestMapping("/api")
public class GatepassController {
    private final RequestorRepository requestorRepo;

    public GatepassController(RequestorRepository requestorRepo) {
        this.requestorRepo = requestorRepo;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitJson(@RequestBody RequestorDTO form) { 
        if (form.getEquipmentItems() == null) {
            return ResponseEntity.badRequest().body("equipmentItems is required");
        }
        
        Requestors requestor = new Requestors();
        requestor.setName(form.name);
        requestor.setDestination(form.destination);
        requestor.setPeriod(form.period);

        var items = form.equipmentItems.stream().map(itemDto -> {
            Equipments item = new Equipments();
            item.setEquipmentName(itemDto.equipmentName);
            item.setQuantity(itemDto.quantity);
            item.setEquipmentCode(itemDto.equipmentCode);
            item.setRequestor(requestor);
            return item;
        }).collect(Collectors.toList());

        requestor.setEquipment(items);

        Requestors saved = requestorRepo.save(requestor);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/requestors")
    public ResponseEntity<List<Requestors>> getAllRequestors() {
        List<Requestors> requestors = requestorRepo.findAll();
        return ResponseEntity.ok(requestors);
    }
}
