package com.gatepass.backend.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.EquipmentDTO;
import com.gatepass.backend.Data.GatepassVerificationDTO;
import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Data.RequestorEquipmentViewDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Service.GatepassService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class GatepassController {
    private final RequestorRepository requestorRepo;
    private final GatepassRepository gatepassRepo;
    private final GatepassService gatepassService;
    private final ZoneId storageZone;
    private final ZoneId displayZone;

    public GatepassController(
            RequestorRepository requestorRepo,
            GatepassRepository gatepassRepo,
            GatepassService gatepassService,
            @Value("${app.timezone.storage:UTC}") String storageTimezone,
            @Value("${app.timezone.display:Asia/Manila}") String displayTimezone) {
        this.requestorRepo = requestorRepo;
        this.gatepassRepo = gatepassRepo;
        this.gatepassService = gatepassService;
        this.storageZone = ZoneId.of(storageTimezone);
        this.displayZone = ZoneId.of(displayTimezone);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitJson(@Valid @RequestBody RequestorDTO form) {
        try {
            byte[] pdf = gatepassService.submitRequest(form);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gatepass.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/requestors")
    public ResponseEntity<List<RequestorEquipmentViewDTO>> getAllRequestors() {
        List<RequestorEquipmentViewDTO> requestors = requestorRepo.findAll().stream()
                .map(this::toRequestorEquipmentView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestors);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verify(@PathVariable String token) {

        Gatepass gatepass = gatepassRepo.findByQrToken(token);

        if (gatepass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }

        GatepassVerificationDTO dto = new GatepassVerificationDTO();
        dto.setReleased(gatepass.isReleased());
        dto.setReturned(gatepass.isReturned());
        dto.setRequestor(gatepass.getRequestor().getName());
        dto.setEquipment(gatepass.getEquipments().stream()
                .map(this::toEquipmentDto)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/verify/{token}/release")
    public ResponseEntity<?> release(@PathVariable String token) {

        Gatepass gatepass = gatepassRepo.findByQrToken(token);

        if (gatepass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }

        if (gatepass.isReleased()) {
            return ResponseEntity.badRequest().body("Already released");
        }

        gatepass.setReleased(true);
        gatepass.setReleasedAt(OffsetDateTime.now(storageZone));
        gatepassRepo.save(gatepass);

        return ResponseEntity.ok("Items released");
    }

    @PostMapping("/verify/{token}/return")
    public ResponseEntity<?> returned(@PathVariable String token) {

        Gatepass gatepass = gatepassRepo.findByQrToken(token);

        if (gatepass == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }

        if (!gatepass.isReleased()) {
            return ResponseEntity.badRequest().body("Not yet released");
        }

        gatepass.setReturned(true);
        gatepass.setReturnedAt(OffsetDateTime.now(storageZone));
        gatepassRepo.save(gatepass);

        return ResponseEntity.ok("Items returned");
    }

    private RequestorEquipmentViewDTO toRequestorEquipmentView(Requestors requestor) {
        RequestorEquipmentViewDTO dto = new RequestorEquipmentViewDTO();
        dto.setId(requestor.getId());
        dto.setName(requestor.getName());
        dto.setDestination(requestor.getDestination());
        dto.setPeriod(requestor.getPeriod());

        List<EquipmentDTO> equipment = requestor.getEquipment() == null ? List.of()
                : requestor.getEquipment().stream()
                        .map(this::toEquipmentDto)
                        .collect(Collectors.toList());
        dto.setEquipment(equipment);
        return dto;
    }

    private EquipmentDTO toEquipmentDto(Equipments equipment) {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(equipment.getId());
        dto.setEquipmentName(equipment.getEquipmentName());
        dto.setQuantity(equipment.getQuantity());
        dto.setEquipmentCode(equipment.getEquipmentCode());

        Gatepass gatepass = equipment.getGatepass();
        if (gatepass != null) {
            dto.setReleased(gatepass.isReleased());
            dto.setReturned(gatepass.isReturned());
            dto.setReleasedAt(toDisplayTime(gatepass.getReleasedAt()));
            dto.setReturnedAt(toDisplayTime(gatepass.getReturnedAt()));
        }

        return dto;
    }

    private OffsetDateTime toDisplayTime(OffsetDateTime timestamp) {
        return timestamp == null ? null : timestamp.withOffsetSameInstant(displayZone.getRules().getOffset(timestamp.toInstant()));
    }
}
