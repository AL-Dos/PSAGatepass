package com.gatepass.backend.Service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.gatepass.backend.Model.Equipments;
import com.gatepass.backend.Model.Gatepass;
import com.gatepass.backend.Model.Requestors;
import com.gatepass.backend.Repository.EquipmentRepository;
import com.gatepass.backend.Repository.GatepassRepository;
import com.gatepass.backend.Repository.RequestorRepository;

@SpringBootTest
@Transactional
public class GatepassArchiveTests {

    @Autowired
    private GatepassService gatepassService;

    @Autowired
    private RequestorRepository requestorRepo;

    @Autowired
    private GatepassRepository gatepassRepo;

    @Autowired
    private EquipmentRepository equipmentRepo;

    @Test
    void archiveAndUnarchiveByEquipmentIds() {
        Requestors r = new Requestors();
        r.setName("Test User");
        r.setDestination("Office");
        r.setPeriod("1 day");
        r.setPurpose("Testing");
        Requestors savedR = requestorRepo.save(r);

        Gatepass gp = new Gatepass();
        gp.setQrToken("test-token-123");
        gp.setRequestor(savedR);
        Gatepass savedGp = gatepassRepo.save(gp);

        Equipments e = new Equipments();
        e.setEquipmentName("Camera");
        e.setQuantity(1);
        e.setEquipmentCode("CAM-001");
        e.setGatepass(savedGp);
        e.setRequestor(savedR);
        Equipments savedE = equipmentRepo.save(e);

        // Ensure initial state is not archived
        Gatepass before = gatepassRepo.findById(savedGp.getId()).orElseThrow();
        assertFalse(before.isArchived());

        // Archive
        gatepassService.archiveByEquipmentIds(List.of(savedE.getId()));
        Gatepass afterArchive = gatepassRepo.findById(savedGp.getId()).orElseThrow();
        assertTrue(afterArchive.isArchived());

        // Unarchive
        gatepassService.unarchiveByEquipmentIds(List.of(savedE.getId()));
        Gatepass afterUnarchive = gatepassRepo.findById(savedGp.getId()).orElseThrow();
        assertFalse(afterUnarchive.isArchived());
    }
}
