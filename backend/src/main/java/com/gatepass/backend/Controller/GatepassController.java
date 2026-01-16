package com.gatepass.backend.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.gatepass.backend.Data.RequestorDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if (form.getEquipmentItems() == null) {
            return ResponseEntity.badRequest().body("equipmentItems is required");
        }
        
        Requestors requestor = new Requestors();
        requestor.setName(form.name);
        requestor.setDestination(form.destination);
        requestor.setPeriod(form.period);

        Gatepass gatepass = new Gatepass();
        gatepass.setQrToken(UUID.randomUUID().toString());
        gatepass.setRequestor(requestor);

        var items = form.equipmentItems.stream().map(itemDto -> {
            Equipments item = new Equipments();
            item.setEquipmentName(itemDto.equipmentName);
            item.setQuantity(itemDto.quantity);
            item.setEquipmentCode(itemDto.equipmentCode);
            item.setGatepass(gatepass);
            return item;
        }).toList();

        requestor.setEquipment(items);
        gatepass.setEquipments(items);

        requestorRepo.save(requestor);

        String qrUrl = "http://localhost:4200/verify/" + gatepass.getQrToken();

        BufferedImage qrImage = QrCodeUtil.generateQr(qrUrl, 300);

        byte[] pdf = GatepassUtil.createMultiItemPdf(qrImage, items);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gatepass.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/requestors")
    public ResponseEntity<List<Requestors>> getAllRequestors() {
        List<Requestors> requestors = requestorRepo.findAll();
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
            "equipment", gatepass.getEquipments()
        ));
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
        gatepass.setReleasedAt(LocalDateTime.now());
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
        gatepass.setReturnedAt(LocalDateTime.now());
        gatepassRepo.save(gatepass);

        return ResponseEntity.ok("Items returned");
    }
}
