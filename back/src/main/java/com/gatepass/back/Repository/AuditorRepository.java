package com.gatepass.back.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatepass.back.Model.Auditors;

public interface AuditorRepository extends JpaRepository<Auditors, Long> {
    Optional<Auditors> findByName(String name);
}
