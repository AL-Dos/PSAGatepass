package com.gatepass.backend.Controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatepass.backend.Data.ExportLogsRequest;
import com.gatepass.backend.Model.Auditors;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Repository.AuditorRepository;
import com.gatepass.backend.Repository.EquipmentRepository;
import com.gatepass.backend.Service.TransmittalService;

@RestController
@RequestMapping("/api/logs")
public class TransmittalController {
    private final TransmittalService transmittalService;
    private final EquipmentRepository equipmentRepository;
    private final AuditorRepository auditorRepository;

    public TransmittalController(
            TransmittalService transmittalService,
            EquipmentRepository equipmentRepository,
            AuditorRepository auditorRepository) {
        this.transmittalService = transmittalService;
        this.equipmentRepository = equipmentRepository;
        this.auditorRepository = auditorRepository;
    }

    @PostMapping("/export")
    public ResponseEntity<?> exportLogs(@RequestBody ExportLogsRequest request, Authentication authentication) {
        List<Long> equipmentIds = request == null ? null : request.getEquipmentIds();
        List<Equipments> equipments = (equipmentIds == null || equipmentIds.isEmpty())
                ? equipmentRepository.findAll()
                : equipmentRepository.findByIdIn(equipmentIds);
        if (equipments.isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment found for export");
        }

        String auditorName = resolveAuditorName(authentication);
        byte[] pdf = transmittalService.exportLogsPdf(equipments, auditorName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transmittal.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private String resolveAuditorName(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "Unknown Auditor";
        }

        return auditorRepository.findByName(authentication.getName())
                .map(Auditors::getName)
                .orElse(authentication.getName());
    }
}
