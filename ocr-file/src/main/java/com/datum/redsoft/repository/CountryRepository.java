package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Country;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Repositorio para la entidad Country usando Panache
 */
@ApplicationScoped
public class CountryRepository implements PanacheRepository<Country> {

    /**
     * Busca un país por su código ISO
     * @param isoCode código ISO del país
     * @return Optional con el país encontrado
     */
    public Optional<Country> findByIsoCode(String isoCode) {
        return find("isoCode", isoCode).firstResultOptional();
    }

    /**
     * Busca países por nombre (búsqueda parcial, case insensitive)
     * @param name nombre o parte del nombre
     * @return lista de países que coinciden
     */
    public java.util.List<Country> findByNameContaining(String name) {
        return find("UPPER(name) LIKE UPPER(?1)", "%" + name + "%").list();
    }

    /**
     * Verifica si existe un país con el código ISO dado
     * @param isoCode código ISO
     * @return true si existe, false en caso contrario
     */
    public boolean existsByIsoCode(String isoCode) {
        return count("isoCode", isoCode) > 0;
    }
}