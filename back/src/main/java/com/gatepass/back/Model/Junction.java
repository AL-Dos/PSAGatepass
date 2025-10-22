package com.gatepass.back.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "junction")
public class Junction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;
    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;
    @Column(name = "custodian_id", nullable = false)
    private Long custodianId;
    @Column(name = "css-oic_id", nullable = false)
    private Long cssOicId;
    @Column(name = "guard_check", nullable = false)
    private Boolean guardCheck = false;
}