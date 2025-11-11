package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CountryCreateRequest;
import com.datum.redsoft.dto.request.CountryUpdateRequest;
import com.datum.redsoft.dto.response.CountryDTO;
import com.datum.redsoft.entity.Country;

import java.util.List;
import java.util.Optional;

/**
 * Interface para el servicio de gestión de países
 * Define los métodos principales para operaciones CRUD de Country
 */
public interface ICountryService {
    
    /**
     * Obtiene todos los países
     * @return Lista de CountryDTO
     */
    List<CountryDTO> getAllCountries();
    
    /**
     * Obtiene un país por ID
     * @param id ID del país
     * @return Optional con CountryDTO si existe
     */
    Optional<CountryDTO> getCountryById(Long id);
    
    /**
     * Obtiene un país por código ISO
     * @param isoCode Código ISO del país (ej: "US", "PE", "MX")
     * @return Optional con CountryDTO si existe
     */
    Optional<CountryDTO> getCountryByIsoCode(String isoCode);
    
    /**
     * Busca países por nombre
     * @param name Nombre o parte del nombre a buscar
     * @return Lista de CountryDTO que coinciden con la búsqueda
     */
    List<CountryDTO> searchCountriesByName(String name);
    
    /**
     * Crea un nuevo país
     * @param request Datos del nuevo país
     * @return CountryDTO del país creado
     */
    CountryDTO createCountry(CountryCreateRequest request);
    
    /**
     * Actualiza un país existente
     * @param id ID del país a actualizar
     * @param request Datos actualizados
     * @return Optional con CountryDTO actualizado si existe
     */
    Optional<CountryDTO> updateCountry(Long id, CountryUpdateRequest request);
    
    /**
     * Elimina un país
     * @param id ID del país a eliminar
     * @return true si se eliminó correctamente, false si no existe
     */
    boolean deleteCountry(Long id);
    
    /**
     * Obtiene la entidad Country por ID (para uso interno)
     * @param id ID del país
     * @return Optional con la entidad Country si existe
     */
    Optional<Country> getCountryEntityById(Long id);
}