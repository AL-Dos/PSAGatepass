package com.gatepass.backend.Model;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

    @Column(name = "qr_token", nullable = false, unique = true)
    private String qrToken;

    @Column(name = "released", nullable = false)
    private boolean released = false;

    @Column(name = "returned", nullable = false)
    private boolean returned = false;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;
    @Column(name = "returned_at")
    private OffsetDateTime returnedAt;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private Requestors requestor;

    @OneToMany(mappedBy = "gatepass")
    @JsonBackReference
    private List<Equipments> equipments;
    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;

}
