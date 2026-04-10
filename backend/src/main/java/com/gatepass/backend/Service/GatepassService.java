package com.gatepass.backend.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatepass.backend.Data.RequestorDTO;
import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;
import com.gatepass.backend.Util.GatepassUtil;
import com.gatepass.backend.Util.QrCodeUtil;

@Service
public class GatepassService {

    private final RequestorRepository requestorRepo;
    private final GatepassRepository gatepassRepo;
    private final String verifyBaseUrl;

    public GatepassService(
            RequestorRepository requestorRepo,
            GatepassRepository gatepassRepo,
            @Value("${app.qr.verify-base-url:http://localhost:8080/api/verify/}") String verifyBaseUrl) {
        this.requestorRepo = requestorRepo;
        this.gatepassRepo = gatepassRepo;
        this.verifyBaseUrl = verifyBaseUrl.endsWith("/") ? verifyBaseUrl : verifyBaseUrl + "/";
    }

    @Transactional
    public byte[] submitRequest(RequestorDTO form) {
        Requestors requestor = new Requestors();
        requestor.setName(form.getName());
        requestor.setDestination(form.getDestination());
        requestor.setPeriod(form.getPeriod());
        requestor.setPurpose(form.getPurpose());

        // Save Requestor first to generate ID
        final Requestors savedRequestor = requestorRepo.save(requestor);

        Gatepass gatepass = new Gatepass();
        gatepass.setQrToken(UUID.randomUUID().toString());
        gatepass.setRequestor(savedRequestor);

        // Save Gatepass to generate ID
        final Gatepass savedGatepass = gatepassRepo.save(gatepass);

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

        String qrUrl = verifyBaseUrl + savedGatepass.getQrToken();
        BufferedImage qrImage = QrCodeUtil.generateQr(qrUrl, 300);
        byte[] pdf = GatepassUtil.createMultiItemPdf(qrImage, items);
        return pdf;
    }
}