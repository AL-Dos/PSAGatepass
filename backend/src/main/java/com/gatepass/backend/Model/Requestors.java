package com.gatepass.backend.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "requestors")
public class Requestors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "destination", nullable = false)
    private String destination;
    @Column(name = "period", nullable = false)
    private String period;
    @Column(name = "purpose", nullable = false)
    private String purpose;

    @OneToMany(mappedBy = "requestor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Equipments> equipment;
}
