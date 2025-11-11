package com.datum.redsoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa las empresas del grupo corporativo asociadas a un país.
 * Objetivo: Relacionar tarjetas, usuarios y gastos a una entidad jurídica en un país específico.
 */
@Entity
@Table(name = "Company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre legal de la empresa
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Vínculo con el país donde está registrada la empresa
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    /**
     * Dirección fiscal
     */
    @Column(name = "address", length = 500)
    private String address;
}