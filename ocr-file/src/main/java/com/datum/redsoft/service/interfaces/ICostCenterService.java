package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CreateCostCenterRequest;
import com.datum.redsoft.dto.request.UpdateCostCenterRequest;
import com.datum.redsoft.dto.response.CostCenterResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de centros de costo
 * Define los contratos para la gestión de centros de costo
 */
public interface ICostCenterService {
    
    /**
     * Obtiene todos los centros de costo
     */
    List<CostCenterResponseDTO> getAllCostCenters();
    
    /**
     * Obtiene un centro de costo por ID
     */
    Optional<CostCenterResponseDTO> getCostCenterById(Long id);
    
    /**
     * Obtiene un centro de costo por código
     */
    Optional<CostCenterResponseDTO> getCostCenterByCode(String code);
    
    /**
     * Obtiene solo los centros de costo activos
     */
    List<CostCenterResponseDTO> getActiveCostCenters();
    
    /**
     * Busca centros de costo por nombre
     */
    List<CostCenterResponseDTO> searchByName(String name);
    
    /**
     * Crea un nuevo centro de costo
     */
    CostCenterResponseDTO createCostCenter(CreateCostCenterRequest request);
    
    /**
     * Actualiza un centro de costo existente
     */
    Optional<CostCenterResponseDTO> updateCostCenter(Long id, UpdateCostCenterRequest request);
    
    /**
     * Elimina un centro de costo
     */
    boolean deleteCostCenter(Long id);
    
    /**
     * Activa un centro de costo
     */
    boolean activateCostCenter(Long id);
    
    /**
     * Desactiva un centro de costo
     */
    boolean deactivateCostCenter(Long id);
}
