package com.gatepass.backend.Service;

import java.awt.image.BufferedImage;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.EquipmentRepository;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Util.GatepassUtil;
import com.gatepass.backend.Util.QrCodeUtil;

@Service
public class GatepassService {
    private static final Logger log = LoggerFactory.getLogger(GatepassService.class);

    private final RequestorRepository requestorRepo;
    private final GatepassRepository gatepassRepo;
    private final EquipmentRepository equipmentRepo;
    private final String verifyBaseUrl;
    private final ZoneId storageZone;

    public GatepassService(
            RequestorRepository requestorRepo,
            GatepassRepository gatepassRepo,
            EquipmentRepository equipmentRepo,
            @Value("${app.qr.verify-base-url:http://localhost:8080/api/verify/}") String verifyBaseUrl,
            @Value("${app.timezone.storage:UTC}") String storageTimezone) {
        this.requestorRepo = requestorRepo;
        this.gatepassRepo = gatepassRepo;
        this.equipmentRepo = equipmentRepo;
        this.verifyBaseUrl = verifyBaseUrl.endsWith("/") ? verifyBaseUrl : verifyBaseUrl + "/";
        this.storageZone = ZoneId.of(storageTimezone);
    }

    @Transactional
    public byte[] submitRequest(RequestorDTO form) {
        log.info("Gatepass submit initiated for requestor: {}", form.getName());
        
        Requestors requestor = new Requestors();
        requestor.setName(form.getName());
        requestor.setDestination(form.getDestination());
        requestor.setPeriod(form.getPeriod());
        requestor.setPurpose(form.getPurpose());

        // Save Requestor first to generate ID
        final Requestors savedRequestor = requestorRepo.save(requestor);
        log.debug("Requestor created with ID: {}", savedRequestor.getId());

        Gatepass gatepass = new Gatepass();
        gatepass.setQrToken(UUID.randomUUID().toString());
        gatepass.setRequestor(savedRequestor);

        // Save Gatepass to generate ID
        final Gatepass savedGatepass = gatepassRepo.save(gatepass);
        log.debug("Gatepass created with token: {}", savedGatepass.getQrToken());

        List<Equipments> items = form.getEquipment().stream().map(itemDto -> {
            Equipments item = new Equipments();
            item.setEquipmentName(itemDto.getEquipmentName());
            item.setQuantity(itemDto.getQuantity());
            item.setEquipmentCode(itemDto.getEquipmentCode());
            item.setGatepass(savedGatepass);
            item.setRequestor(savedRequestor);
            return item;
        }).collect(Collectors.toList());

        savedRequestor.setEquipment(items);
        savedGatepass.setEquipments(items);

        // Save Requestor again to cascade save Equipments
        requestorRepo.save(savedRequestor);
        log.info("Gatepass submit completed for requestor: {} with {} equipment items", form.getName(), items.size());

        String qrUrl = verifyBaseUrl + savedGatepass.getQrToken();
        BufferedImage qrImage = QrCodeUtil.generateQr(qrUrl, 300);
        byte[] pdf = GatepassUtil.createMultiItemPdf(qrImage, items);
        return pdf;
    }

    @Transactional
    public void archiveByEquipmentIds(List<Long> equipmentIds) {
        List<Equipments> equipments = equipmentRepo.findByIdIn(equipmentIds == null ? List.of() : equipmentIds);
        for (Equipments equipment : equipments) {
            Gatepass gp = equipment.getGatepass();
            if (gp == null) continue;
            if (!gp.isArchived()) {
                gp.setArchived(true);
                gp.setArchivedAt(OffsetDateTime.now(storageZone));
                gatepassRepo.save(gp);
            }
        }
    }

    @Transactional
    public void unarchiveByEquipmentIds(List<Long> equipmentIds) {
        List<Equipments> equipments = equipmentRepo.findByIdIn(equipmentIds == null ? List.of() : equipmentIds);
        for (Equipments equipment : equipments) {
            Gatepass gp = equipment.getGatepass();
            if (gp == null) continue;
            if (gp.isArchived()) {
                gp.setArchived(false);
                gp.setArchivedAt(null);
                gatepassRepo.save(gp);
            }
        }
    }
}