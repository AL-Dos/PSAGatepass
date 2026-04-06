package com.gatepass.backend.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.EquipmentDTO;
import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Data.RequestorEquipmentViewDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Util.GatepassUtil;
import com.gatepass.backend.Util.QrCodeUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.awt.image.BufferedImage;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class GatepassController {
    private final RequestorRepository requestorRepo;
    private final GatepassRepository gatepassRepo;

    public GatepassController(RequestorRepository requestorRepo, GatepassRepository gatepassRepo) {
        this.requestorRepo = requestorRepo;
        this.gatepassRepo = gatepassRepo;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitJson(@RequestBody RequestorDTO form) {
        if (form.getEquipment() == null) {
            return ResponseEntity.badRequest().body("equipment is required");
        }

        Requestors requestor = new Requestors();
        requestor.setName(form.name);
        requestor.setDestination(form.destination);
        requestor.setPeriod(form.period);

        // Save Requestor first to generate ID
        requestor = requestorRepo.save(requestor);

        Gatepass gatepass = new Gatepass();
        gatepass.setQrToken(UUID.randomUUID().toString());
        gatepass.setRequestor(requestor);

        // Save Gatepass to generate ID
        gatepass = gatepassRepo.save(gatepass);

        // Capture effectively final variables for lambda
        final Requestors savedRequestor = requestor;
        final Gatepass savedGatepass = gatepass;

        var items = form.getEquipment().stream().map(itemDto -> {
            Equipments item = new Equipments();
            item.setEquipmentName(itemDto.equipmentName);
            item.setQuantity(itemDto.quantity);
            item.setEquipmentCode(itemDto.equipmentCode);
            item.setGatepass(savedGatepass);
            item.setRequestor(savedRequestor);
            return item;
        }).collect(java.util.stream.Collectors.toList());

        savedRequestor.setEquipment(items);
        savedGatepass.setEquipments(items);

        // Save Requestor again to cascade save Equipments
        requestorRepo.save(savedRequestor);

        String qrUrl = "http://localhost:4200/verify/" + gatepass.getQrToken();

        BufferedImage qrImage = QrCodeUtil.generateQr(qrUrl, 300);

        byte[] pdf = GatepassUtil.createMultiItemPdf(qrImage, items);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gatepass.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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

        return ResponseEntity.ok(Map.of(
                "released", gatepass.isReleased(),
                "returned", gatepass.isReturned(),
                "requestor", gatepass.getRequestor().getName(),
                "equipment", gatepass.getEquipments()));
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
        gatepass.setReleasedAt(OffsetDateTime.now(ZoneId.of("Asia/Manila")));
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
        gatepass.setReturnedAt(OffsetDateTime.now(ZoneId.of("Asia/Manila")));
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
            dto.setReleasedAt(gatepass.getReleasedAt());
            dto.setReturnedAt(gatepass.getReturnedAt());
        }

        return dto;
    }
}
