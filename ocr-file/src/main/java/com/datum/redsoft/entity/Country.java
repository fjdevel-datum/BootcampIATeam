package com.datum.redsoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Tabla de referencia para almacenar los países en los que opera la empresa.
 * Objetivo: Evitar redundancia de nombres de países y asegurar consistencia mediante códigos ISO.
 */
@Entity
@Table(name = "Country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código estandarizado (ejemplo: SLV, HND, GT, PA, Dominicana, Costarica)
     */
    @Column(name = "iso_code", nullable = false, unique = true, length = 10)
    private String isoCode;

    /**
     * Nombre completo del país
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Company> companies;

}