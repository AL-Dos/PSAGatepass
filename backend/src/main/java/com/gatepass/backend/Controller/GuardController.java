package com.gatepass.backend.Controller;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatepass.backend.Model.Gatepass;

import com.gatepass.backend.Repository.GatepassRepository;

@RestController
@RequestMapping("/api/guard")
public class GuardController {
    private static final Logger log = LoggerFactory.getLogger(GuardController.class);
    
    private final GatepassRepository gatepassRepo;
    private final ZoneId storageZone;

    public GuardController(
            GatepassRepository gatepassRepo,
            @Value("${app.timezone.storage:UTC}") String storageTimezone) {
        this.gatepassRepo = gatepassRepo;
        this.storageZone = ZoneId.of(storageTimezone);
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scan(@RequestBody com.gatepass.backend.Data.ScanRequestDTO dto) {
        if (dto.getQrToken() == null || dto.getQrToken().isBlank()) {
            log.warn("Scan request with missing QR token");
            return ResponseEntity.badRequest().body("Missing QR Token");
        }

        Gatepass gatepass = gatepassRepo.findByQrTokenAndArchivedFalse(dto.getQrToken());
        if (gatepass == null) {
            log.warn("Scan attempt with invalid QR token");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR Code");
        }
        String action = "";

        if (!gatepass.isReleased()) {
            gatepass.setReleased(true);
            gatepass.setReleasedAt(OffsetDateTime.now(storageZone));
            action = "Released";
            log.info("Gatepass released by guard: {} for requestor: {}", gatepass.getRequestor().getName());
        } else if (!gatepass.isReturned()) {
            gatepass.setReturned(true);
            gatepass.setReturnedAt(OffsetDateTime.now(storageZone));
            action = "Returned";
            log.info("Gatepass returned by guard: {} for requestor: {}", gatepass.getRequestor().getName());
        } else {
            log.warn("Scan attempt on completed gatepass by guard: {}", gatepass.getRequestor().getName());
            return ResponseEntity.badRequest().body("Gatepass already completed (Returned)");
        }

        gatepassRepo.save(gatepass);

        return ResponseEntity.ok(Map.of(
                "message", "Success: Items " + action,
                "action", action,
                "gatepass", gatepass));
    }
}
