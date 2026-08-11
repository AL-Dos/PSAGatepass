package com.gatepass.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatepass.backend.Model.Gatepass;

public interface GatepassRepository extends JpaRepository<Gatepass, Long>{
    Gatepass findByQrToken(String qrToken);
    Gatepass findByQrTokenAndArchivedFalse(String qrToken);
    List<Gatepass> findByArchivedTrue();
    List<Gatepass> findByArchivedFalse();
}
