package com.gatepass.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatepass.backend.Model.Equipments;

public interface EquipmentRepository extends JpaRepository<Equipments, Long> {

}
