package com.gatepass.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gatepass.backend.Model.Guard;
import java.util.Optional;

@Repository
public interface GuardRepository extends JpaRepository<Guard, Long> {
    Optional<Guard> findByName(String name);
}
