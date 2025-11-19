package com.gatepass.backend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "equipments")
public class Equipments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "equipment_code", nullable = false)
    private String equipmentCode;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    @JsonBackReference
    private Requestors requestor;
}
