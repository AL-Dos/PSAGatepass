package com.gatepass.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gatepass.backend.Model.Gatepass;

@Repository
public interface GatepassRepository extends JpaRepository<Gatepass, Long>{
    Gatepass findByQrToken(String qrToken);
}
