package com.gatepass.backend.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gatepasses")
public class Gatepass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requestor_id")
    private Requestors requestor;

    @Column(name = "date_issued", nullable = false)
    private LocalDate dateIssued = LocalDate.now();

    @OneToMany(mappedBy = "gatepass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatepassItems> items = new ArrayList<>();
}
