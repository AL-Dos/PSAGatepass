package com.gatepass.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.gatepass.backend.Model.Equipments;

public interface EquipmentRepository extends JpaRepository<Equipments, Long> {
    @EntityGraph(attributePaths = { "requestor" })
    List<Equipments> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = { "requestor" })
    @NonNull
    List<Equipments> findAll();
}
