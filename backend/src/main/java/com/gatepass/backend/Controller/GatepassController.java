package com.gatepass.backend.Controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.AuditorRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Util.RequestorMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class GatepassController {
    private final RequestorRepository requestorRepo;
    private final RequestorMapper requestorMapper;

    public GatepassController(RequestorRepository requestorRepo, RequestorMapper requestorMapper) {
        this.requestorRepo = requestorRepo;
        this.requestorMapper = requestorMapper;
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

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('OIC','CUSTODIAN','GUARD')")
    public ResponseEntity<?> getAllRequestor() {
        var list = requestorRepo.findAll();
        return ResponseEntity.ok(requestorMapper.toDtoList(list));
    }

    @GetMapping("/listById/{id}")
    @PreAuthorize("hasAnyRole('OIC','CUSTODIAN','GUARD')")
    public ResponseEntity<?> getRequestorById(@PathVariable Long id) {
        return requestorRepo.findById(id).map(requestorMapper::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/list/search")
    @PreAuthorize("hasAnyRole('OIC','CUSTODIAN','GUARD')")
    public ResponseEntity<?> searchbyName(@RequestParam("q") String query) {
        var list = requestorRepo.findByNameContainingIgnoreCase(query);
        return ResponseEntity.ok(requestorMapper.toDtoList(list));
    }

    // Approve: OIC only
    @PostMapping("/list/{id}/approve")
    @PreAuthorize("hasRole('OIC')")
    public ResponseEntity<?> approve(@PathVariable Long id,  AuditorRepository auditorRepo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Requestors req = requestorRepo.findById(id).orElseThrow();
        req.setApprovedBy(username);
        requestorRepo.save(req);
        
        return ResponseEntity.ok(requestorMapper.toDto(req));
    }

    // Note: Custodian only
    @PostMapping("/list/{id}/note")
    @PreAuthorize("hasRole('CUSTODIAN')")
    public ResponseEntity<?> note(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Requestors req = requestorRepo.findById(id).orElseThrow();
        req.setNotedBy(username);
        requestorRepo.save(req);

        return ResponseEntity.ok(requestorMapper.toDto(req));
    }

    // Return check: Guard only
    @PostMapping("/list/{id}/return-check")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<?> returnCheck(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Requestors req = requestorRepo.findById(id).orElseThrow();
        req.setReturnedCheck(username);
        requestorRepo.save(req);

        return ResponseEntity.ok(requestorMapper.toDto(req));
    }   
}
