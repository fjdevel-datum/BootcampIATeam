package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CountryCreateRequest;
import com.datum.redsoft.dto.request.CountryUpdateRequest;
import com.datum.redsoft.dto.response.CountryDTO;
import com.datum.redsoft.entity.Country;
import com.datum.redsoft.repository.CountryRepository;
import com.datum.redsoft.service.interfaces.ICountryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones CRUD de Country
 */
@ApplicationScoped
public class CountryService implements ICountryService {

    @Inject
    CountryRepository countryRepository;

    /**
     * Obtiene todos los países
     */
    @Override
    public List<CountryDTO> getAllCountries() {
        return countryRepository.listAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un país por ID
     */

    public Optional<CountryDTO> getCountryById(Long id) {
        return countryRepository.findByIdOptional(id)
                .map(this::toDTO);
    }

    /**
     * Obtiene un país por código ISO
     */

    public Optional<CountryDTO> getCountryByIsoCode(String isoCode) {
        return countryRepository.findByIsoCode(isoCode)
                .map(this::toDTO);
    }

    /**
     * Busca países por nombre
     */
    @Override
    public List<CountryDTO> searchCountriesByName(String name) {
        return countryRepository.findByNameContaining(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo país
     */
    @Override
    @Transactional
    public CountryDTO createCountry(CountryCreateRequest request) {
        // Verificar que no exista un país con el mismo código ISO
        if (countryRepository.existsByIsoCode(request.getIsoCode())) {
            throw new IllegalArgumentException("Ya existe un país con el código ISO: " + request.getIsoCode());
        }

        Country country = new Country();
        country.setIsoCode(request.getIsoCode().toUpperCase());
        country.setName(request.getName());

        countryRepository.persist(country);
        return toDTO(country);
    }

    /**
     * Actualiza un país existente
     */
    @Override
    @Transactional
    public Optional<CountryDTO> updateCountry(Long id, CountryUpdateRequest request) {
        Optional<Country> countryOpt = countryRepository.findByIdOptional(id);
        
        if (countryOpt.isEmpty()) {
            return Optional.empty();
        }

        Country country = countryOpt.get();

        // Verificar código ISO si se está actualizando
        if (request.getIsoCode() != null && !request.getIsoCode().equals(country.getIsoCode())) {
            if (countryRepository.existsByIsoCode(request.getIsoCode())) {
                throw new IllegalArgumentException("Ya existe un país con el código ISO: " + request.getIsoCode());
            }
            country.setIsoCode(request.getIsoCode().toUpperCase());
        }

        if (request.getName() != null) {
            country.setName(request.getName());
        }

        countryRepository.persist(country);
        return Optional.of(toDTO(country));
    }

    /**
     * Elimina un país
     */
    @Override
    @Transactional
    public boolean deleteCountry(Long id) {
        return countryRepository.deleteById(id);
    }

    /**
     * Convierte una entidad Country a CountryDTO
     */
    private CountryDTO toDTO(Country country) {
        return new CountryDTO(
                country.getId(),
                country.getIsoCode(),
                country.getName()
        );
    }

    /**
     * Obtiene la entidad Country por ID (para uso interno)
     */
    public Optional<Country> getCountryEntityById(Long id) {
        return countryRepository.findByIdOptional(id);
    }
}