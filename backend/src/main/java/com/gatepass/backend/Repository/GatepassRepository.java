package com.gatepass.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatepass.backend.Model.Gatepass;

public interface GatepassRepository extends JpaRepository<Gatepass, Long> {

}
