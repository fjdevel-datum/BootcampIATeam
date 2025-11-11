package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CompanyCreateRequest;
import com.datum.redsoft.dto.request.CompanyUpdateRequest;
import com.datum.redsoft.dto.response.CompanyDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface para el servicio de gestión de empresas
 * Define los métodos principales para operaciones CRUD de Company
 */
public interface ICompanyService {
    
    /**
     * Obtiene todas las empresas
     * @return Lista de CompanyDTO
     */
    List<CompanyDTO> getAllCompanies();
    
    /**
     * Obtiene una empresa por ID
     * @param id ID de la empresa
     * @return Optional con CompanyDTO si existe
     */
    Optional<CompanyDTO> getCompanyById(Long id);
    
    /**
     * Obtiene empresas por país
     * @param countryId ID del país
     * @return Lista de CompanyDTO del país especificado
     */
    List<CompanyDTO> getCompaniesByCountryId(Long countryId);
    
    /**
     * Busca empresas por nombre
     * @param name Nombre o parte del nombre a buscar
     * @return Lista de CompanyDTO que coinciden con la búsqueda
     */
    List<CompanyDTO> searchCompaniesByName(String name);
    
    /**
     * Crea una nueva empresa
     * @param request Datos de la nueva empresa
     * @return CompanyDTO de la empresa creada
     */
    CompanyDTO createCompany(CompanyCreateRequest request);
    
    /**
     * Actualiza una empresa existente
     * @param id ID de la empresa a actualizar
     * @param request Datos actualizados
     * @return Optional con CompanyDTO actualizado si existe
     */
    Optional<CompanyDTO> updateCompany(Long id, CompanyUpdateRequest request);
    
    /**
     * Elimina una empresa
     * @param id ID de la empresa a eliminar
     * @return true si se eliminó correctamente, false si no existe
     */
    boolean deleteCompany(Long id);
}