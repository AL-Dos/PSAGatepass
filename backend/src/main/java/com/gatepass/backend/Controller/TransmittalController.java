package com.gatepass.backend.Controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatepass.backend.Data.ExportLogsRequest;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Repository.EquipmentRepository;
import com.gatepass.backend.Service.TransmittalService;

@RestController
@RequestMapping("/api/logs")
public class TransmittalController {
    private final TransmittalService transmittalService;
    private final EquipmentRepository equipmentRepository;

    public TransmittalController(
            TransmittalService transmittalService,
            EquipmentRepository equipmentRepository) {
        this.transmittalService = transmittalService;
        this.equipmentRepository = equipmentRepository;
    }

    @PostMapping("/export")
    public ResponseEntity<?> exportLogs(@RequestBody ExportLogsRequest request) {
        List<Long> equipmentIds = request == null ? null : request.getEquipmentIds();
        List<Equipments> equipments = (equipmentIds == null || equipmentIds.isEmpty())
                ? equipmentRepository.findAll()
                : equipmentRepository.findByIdIn(equipmentIds);
        if (equipments.isEmpty()) {
            return ResponseEntity.badRequest().body("No equipment found for export");
        }

        byte[] pdf = transmittalService.exportLogsPdf(equipments);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transmittal.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
