package com.gatepass.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatepass.backend.Model.Requestors;

public interface RequestorRepository extends JpaRepository<Requestors, Long> {
    List<Requestors> findByNameContainingIgnoreCase(String name);
}
