package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CompanyCreateRequest;
import com.datum.redsoft.dto.request.CompanyUpdateRequest;
import com.datum.redsoft.dto.response.CompanyDTO;
import com.datum.redsoft.dto.response.CountryDTO;
import com.datum.redsoft.entity.Company;
import com.datum.redsoft.entity.Country;
import com.datum.redsoft.repository.CompanyRepository;
import com.datum.redsoft.service.interfaces.ICompanyService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones CRUD de Company
 */
@ApplicationScoped
public class CompanyService implements ICompanyService {

    @Inject
    CompanyRepository companyRepository;

    @Inject
    CountryService countryService;

    /**
     * Obtiene todas las empresas
     */
    @Override
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAllWithCountries()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una empresa por ID
     */
    @Override
    public Optional<CompanyDTO> getCompanyById(Long id) {
        return companyRepository.findByIdWithCountry(id)
                .map(this::toDTO);
    }

    /**
     * Obtiene empresas por país
     */
    @Override
    public List<CompanyDTO> getCompaniesByCountryId(Long countryId) {
        return companyRepository.findByCountryId(countryId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca empresas por nombre
     */
    @Override
    public List<CompanyDTO> searchCompaniesByName(String name) {
        return companyRepository.findByNameContaining(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva empresa
     */
    @Override
    @Transactional
    public CompanyDTO createCompany(CompanyCreateRequest request) {
        // Verificar que el país existe
        Optional<Country> countryOpt = countryService.getCountryEntityById(request.getCountryId());
        if (countryOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId());
        }

        Company company = new Company();
        company.setName(request.getName());
        company.setCountry(countryOpt.get());
        company.setAddress(request.getAddress());

        companyRepository.persist(company);
        return toDTO(company);
    }

    /**
     * Actualiza una empresa existente
     */
    @Override
    @Transactional
    public Optional<CompanyDTO> updateCompany(Long id, CompanyUpdateRequest request) {
        Optional<Company> companyOpt = companyRepository.findByIdWithCountry(id);
        
        if (companyOpt.isEmpty()) {
            return Optional.empty();
        }

        Company company = companyOpt.get();

        if (request.getName() != null) {
            company.setName(request.getName());
        }

        if (request.getCountryId() != null) {
            Optional<Country> countryOpt = countryService.getCountryEntityById(request.getCountryId());
            if (countryOpt.isEmpty()) {
                throw new IllegalArgumentException("No se encontró el país con ID: " + request.getCountryId());
            }
            company.setCountry(countryOpt.get());
        }

        if (request.getAddress() != null) {
            company.setAddress(request.getAddress());
        }

        companyRepository.persist(company);
        return Optional.of(toDTO(company));
    }

    /**
     * Elimina una empresa
     */
    @Override
    @Transactional
    public boolean deleteCompany(Long id) {
        return companyRepository.deleteById(id);
    }

    /**
     * Convierte una entidad Company a CompanyDTO
     */
    private CompanyDTO toDTO(Company company) {
        CountryDTO countryDTO = new CountryDTO(
                company.getCountry().getId(),
                company.getCountry().getIsoCode(),
                company.getCountry().getName()
        );

        return new CompanyDTO(
                company.getId(),
                company.getName(),
                countryDTO,
                company.getAddress()
        );
    }
}