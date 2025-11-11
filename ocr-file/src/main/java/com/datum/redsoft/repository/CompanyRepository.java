package com.datum.redsoft.repository;

import com.datum.redsoft.entity.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Company usando Panache
 */
@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

    /**
     * Busca empresas por país
     * @param countryId ID del país
     * @return lista de empresas del país
     */
    public List<Company> findByCountryId(Long countryId) {
        return find("country.id", countryId).list();
    }

    /**
     * Busca empresas por nombre (búsqueda parcial, case insensitive)
     * @param name nombre o parte del nombre
     * @return lista de empresas que coinciden
     */
    public List<Company> findByNameContaining(String name) {
        return find("UPPER(name) LIKE UPPER(?1)", "%" + name + "%").list();
    }

    /**
     * Busca una empresa por nombre exacto
     * @param name nombre exacto de la empresa
     * @return Optional con la empresa encontrada
     */
    public Optional<Company> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * Busca empresas con sus países cargados (evita lazy loading)
     * @return lista de empresas con países
     */
    public List<Company> findAllWithCountries() {
        return find("SELECT c FROM Company c JOIN FETCH c.country").list();
    }

    /**
     * Busca una empresa por ID con su país cargado
     * @param id ID de la empresa
     * @return Optional con la empresa y su país
     */
    public Optional<Company> findByIdWithCountry(Long id) {
        return find("SELECT c FROM Company c JOIN FETCH c.country WHERE c.id = ?1", id)
                .firstResultOptional();
    }
}