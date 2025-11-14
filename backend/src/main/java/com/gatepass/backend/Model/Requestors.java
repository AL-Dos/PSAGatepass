package com.gatepass.backend.Model;

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
@Table(name = "requestors")
public class Requestors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "requestor", nullable = false)
    private String requestor;
    @Column(name = "destinaton", nullable = false)
    private String destinaton;
    @Column(name = "period", nullable = false)
    private String period;
}
