package com.gatepass.backend.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.AdminEquipmentActionRequest;
import com.gatepass.backend.Data.EquipmentDTO;
import com.gatepass.backend.Data.GatepassVerificationDTO;
import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Data.RequestorEquipmentViewDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.EquipmentRepository;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Service.GatepassService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(GatepassController.class);
    
    private final RequestorRepository requestorRepo;
    private final GatepassRepository gatepassRepo;
    private final EquipmentRepository equipmentRepo;
    private final GatepassService gatepassService;
    private final ZoneId storageZone;
    private final ZoneId displayZone;

    public GatepassController(
            RequestorRepository requestorRepo,
            GatepassRepository gatepassRepo,
            EquipmentRepository equipmentRepo,
            GatepassService gatepassService,
            @Value("${app.timezone.storage:UTC}") String storageTimezone,
            @Value("${app.timezone.display:Asia/Manila}") String displayTimezone) {
        this.requestorRepo = requestorRepo;
        this.gatepassRepo = gatepassRepo;
        this.equipmentRepo = equipmentRepo;
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

    @PostMapping("/admin/release")
    public ResponseEntity<?> adminRelease(@RequestBody AdminEquipmentActionRequest request) {
        List<Equipments> equipments = request.getEquipmentIds() == null ? List.of() : equipmentRepo.findByIdIn(request.getEquipmentIds());
        if (equipments.isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment selected");
        }

        for (Equipments equipment : equipments) {
            Gatepass gatepass = equipment.getGatepass();
            if (gatepass == null) {
                continue;
            }
            if (!gatepass.isReleased()) {
                gatepass.setReleased(true);
                gatepass.setReleasedAt(OffsetDateTime.now(storageZone));
                gatepassRepo.save(gatepass);
            }
        }

        return ResponseEntity.ok("Selected entries marked as released");
    }

    @PostMapping("/admin/return")
    public ResponseEntity<?> adminReturn(@RequestBody AdminEquipmentActionRequest request) {
        List<Equipments> equipments = request.getEquipmentIds() == null ? List.of() : equipmentRepo.findByIdIn(request.getEquipmentIds());
        if (equipments.isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment selected");
        }

        for (Equipments equipment : equipments) {
            Gatepass gatepass = equipment.getGatepass();
            if (gatepass == null || !gatepass.isReleased()) {
                continue;
            }
            if (!gatepass.isReturned()) {
                gatepass.setReturned(true);
                gatepass.setReturnedAt(OffsetDateTime.now(storageZone));
                gatepassRepo.save(gatepass);
            }
        }

        return ResponseEntity.ok("Selected entries marked as returned");
    }

    @PostMapping("/admin/delete")
    public ResponseEntity<?> adminDelete(@RequestBody AdminEquipmentActionRequest request) {
        List<Equipments> equipments = request.getEquipmentIds() == null ? List.of() : equipmentRepo.findByIdIn(request.getEquipmentIds());
        if (equipments.isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment selected");
        }

        equipmentRepo.deleteAll(equipments);
        return ResponseEntity.ok("Selected entries deleted");
    }

    @PostMapping("/admin/archive")
    public ResponseEntity<?> adminArchive(@RequestBody AdminEquipmentActionRequest request) {
        if (request.getEquipmentIds() == null || request.getEquipmentIds().isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment selected");
        }
        gatepassService.archiveByEquipmentIds(request.getEquipmentIds());
        return ResponseEntity.ok("Selected entries archived");
    }

    @PostMapping("/admin/unarchive")
    public ResponseEntity<?> adminUnarchive(@RequestBody AdminEquipmentActionRequest request) {
        if (request.getEquipmentIds() == null || request.getEquipmentIds().isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment selected");
        }
        gatepassService.unarchiveByEquipmentIds(request.getEquipmentIds());
        return ResponseEntity.ok("Selected entries unarchived");
    }

    @GetMapping("/admin/archived")
    public ResponseEntity<List<RequestorEquipmentViewDTO>> getArchivedRequestors() {
        List<RequestorEquipmentViewDTO> archived = requestorRepo.findAll().stream()
                .map(this::toArchivedRequestorEquipmentView)
                .filter(dto -> dto.getEquipment() != null && !dto.getEquipment().isEmpty())
                .collect(Collectors.toList());

        return ResponseEntity.ok(archived);
    }

    private RequestorEquipmentViewDTO toArchivedRequestorEquipmentView(Requestors requestor) {
        RequestorEquipmentViewDTO dto = new RequestorEquipmentViewDTO();
        dto.setId(requestor.getId());
        dto.setName(requestor.getName());
        dto.setDestination(requestor.getDestination());
        dto.setPeriod(requestor.getPeriod());

        List<EquipmentDTO> equipment = requestor.getEquipment() == null ? List.of()
                : requestor.getEquipment().stream()
                        .filter(item -> item.getGatepass() != null && item.getGatepass().isArchived())
                        .map(this::toEquipmentDto)
                        .collect(Collectors.toList());
        dto.setEquipment(equipment);
        return dto;
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verify(@PathVariable String token) {
        log.info("Verify request for token: {}", token.substring(0, Math.min(8, token.length())) + "...");

        Gatepass gatepass = gatepassRepo.findByQrTokenAndArchivedFalse(token);

        if (gatepass == null) {
            log.warn("Invalid QR token attempted: {}", token.substring(0, Math.min(8, token.length())) + "...");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }
        
        log.debug("Gatepass verified for requestor: {}", gatepass.getRequestor().getName());

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
        log.info("Release request for token: {}", token.substring(0, Math.min(8, token.length())) + "...");

        Gatepass gatepass = gatepassRepo.findByQrTokenAndArchivedFalse(token);

        if (gatepass == null) {
            log.warn("Invalid token in release request: {}", token.substring(0, Math.min(8, token.length())) + "...");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }

        if (gatepass.isReleased()) {
            log.warn("Duplicate release attempted for gatepass ID: {}", gatepass.getId());
            return ResponseEntity.badRequest().body("Already released");
        }

        gatepass.setReleased(true);
        gatepass.setReleasedAt(OffsetDateTime.now(storageZone));
        gatepassRepo.save(gatepass);
        log.info("Items released for gatepass ID: {} (requestor: {})", gatepass.getId(), gatepass.getRequestor().getName());

        return ResponseEntity.ok("Items released");
    }

    @PostMapping("/verify/{token}/return")
    public ResponseEntity<?> returned(@PathVariable String token) {
        log.info("Return request for token: {}", token.substring(0, Math.min(8, token.length())) + "...");

        Gatepass gatepass = gatepassRepo.findByQrTokenAndArchivedFalse(token);

        if (gatepass == null) {
            log.warn("Invalid token in return request: {}", token.substring(0, Math.min(8, token.length())) + "...");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR");
        }

        if (!gatepass.isReleased()) {
            log.warn("Return attempted on not-yet-released gatepass ID: {}", gatepass.getId());
            return ResponseEntity.badRequest().body("Not yet released");
        }

        gatepass.setReturned(true);
        gatepass.setReturnedAt(OffsetDateTime.now(storageZone));
        gatepassRepo.save(gatepass);
        log.info("Items returned for gatepass ID: {} (requestor: {})", gatepass.getId(), gatepass.getRequestor().getName());

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
                        .filter(item -> item.getGatepass() == null || !item.getGatepass().isArchived())
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
            dto.setArchived(gatepass.isArchived());
            dto.setReleasedAt(toDisplayTime(gatepass.getReleasedAt()));
            dto.setReturnedAt(toDisplayTime(gatepass.getReturnedAt()));
        }

        return dto;
    }

    private OffsetDateTime toDisplayTime(OffsetDateTime timestamp) {
        return timestamp == null ? null : timestamp.withOffsetSameInstant(displayZone.getRules().getOffset(timestamp.toInstant()));
    }
}
