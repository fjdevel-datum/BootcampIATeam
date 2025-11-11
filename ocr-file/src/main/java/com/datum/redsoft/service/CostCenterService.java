package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateCostCenterRequest;
import com.datum.redsoft.dto.request.UpdateCostCenterRequest;
import com.datum.redsoft.dto.response.CostCenterResponseDTO;
import com.datum.redsoft.entity.CostCenter;
import com.datum.redsoft.repository.CostCenterRepository;
import com.datum.redsoft.service.interfaces.ICostCenterService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de centros de costo
 * Maneja toda la lógica de negocio relacionada con los centros de costo
 */
@ApplicationScoped
public class CostCenterService implements ICostCenterService {
    
    private static final Logger logger = Logger.getLogger(CostCenterService.class.getName());
    
    @Inject
    CostCenterRepository costCenterRepository;
    
    @Override
    public List<CostCenterResponseDTO> getAllCostCenters() {
        logger.info("Obteniendo todos los centros de costo");
        return costCenterRepository.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<CostCenterResponseDTO> getCostCenterById(Long id) {
        logger.info("Buscando centro de costo con ID: " + id);
        return costCenterRepository.findByIdOptional(id)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public Optional<CostCenterResponseDTO> getCostCenterByCode(String code) {
        logger.info("Buscando centro de costo con código: " + code);
        return costCenterRepository.findByCode(code)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public List<CostCenterResponseDTO> getActiveCostCenters() {
        logger.info("Obteniendo centros de costo activos");
        return costCenterRepository.findActiveCostCenters().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CostCenterResponseDTO> searchByName(String name) {
        logger.info("Buscando centros de costo por nombre: " + name);
        return costCenterRepository.searchByName(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CostCenterResponseDTO createCostCenter(CreateCostCenterRequest request) {
        logger.info("Creando nuevo centro de costo: " + request.getCode());
        
        // Validar que no exista un centro de costo con el mismo código
        if (costCenterRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Ya existe un centro de costo con el código: " + request.getCode());
        }
        
        CostCenter costCenter = new CostCenter();
        costCenter.setCode(request.getCode());
        costCenter.setName(request.getName());
        costCenter.setDescription(request.getDescription());
        costCenter.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        costCenterRepository.persist(costCenter);
        logger.info("Centro de costo creado exitosamente con ID: " + costCenter.getId());
        
        return convertToResponseDTO(costCenter);
    }
    
    @Override
    @Transactional
    public Optional<CostCenterResponseDTO> updateCostCenter(Long id, UpdateCostCenterRequest request) {
        logger.info("Actualizando centro de costo con ID: " + id);
        
        Optional<CostCenter> costCenterOpt = costCenterRepository.findByIdOptional(id);
        if (costCenterOpt.isEmpty()) {
            logger.warning("Centro de costo no encontrado con ID: " + id);
            return Optional.empty();
        }
        
        CostCenter costCenter = costCenterOpt.get();
        
        // Actualizar campos no nulos
        if (request.getCode() != null && !request.getCode().equals(costCenter.getCode())) {
            // Validar que el nuevo código no exista
            if (costCenterRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("Ya existe un centro de costo con el código: " + request.getCode());
            }
            costCenter.setCode(request.getCode());
        }
        
        if (request.getName() != null) {
            costCenter.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            costCenter.setDescription(request.getDescription());
        }
        
        if (request.getIsActive() != null) {
            costCenter.setIsActive(request.getIsActive());
        }
        
        costCenter.setUpdatedAt(LocalDateTime.now());
        costCenterRepository.merge(costCenter);
        
        logger.info("Centro de costo actualizado exitosamente con ID: " + id);
        return Optional.of(convertToResponseDTO(costCenter));
    }
    
    @Override
    @Transactional
    public boolean deleteCostCenter(Long id) {
        logger.info("Eliminando centro de costo con ID: " + id);
        
        Optional<CostCenter> costCenterOpt = costCenterRepository.findByIdOptional(id);
        if (costCenterOpt.isEmpty()) {
            logger.warning("Centro de costo no encontrado con ID: " + id);
            return false;
        }
        
        costCenterRepository.remove(costCenterOpt.get());
        logger.info("Centro de costo eliminado exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean activateCostCenter(Long id) {
        logger.info("Activando centro de costo con ID: " + id);
        
        Optional<CostCenter> costCenterOpt = costCenterRepository.findByIdOptional(id);
        if (costCenterOpt.isEmpty()) {
            logger.warning("Centro de costo no encontrado con ID: " + id);
            return false;
        }
        
        CostCenter costCenter = costCenterOpt.get();
        costCenter.setIsActive(true);
        costCenter.setUpdatedAt(LocalDateTime.now());
        costCenterRepository.merge(costCenter);
        
        logger.info("Centro de costo activado exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean deactivateCostCenter(Long id) {
        logger.info("Desactivando centro de costo con ID: " + id);
        
        Optional<CostCenter> costCenterOpt = costCenterRepository.findByIdOptional(id);
        if (costCenterOpt.isEmpty()) {
            logger.warning("Centro de costo no encontrado con ID: " + id);
            return false;
        }
        
        CostCenter costCenter = costCenterOpt.get();
        costCenter.setIsActive(false);
        costCenter.setUpdatedAt(LocalDateTime.now());
        costCenterRepository.merge(costCenter);
        
        logger.info("Centro de costo desactivado exitosamente");
        return true;
    }
    
    /**
     * Convierte una entidad CostCenter a CostCenterResponseDTO
     */
    private CostCenterResponseDTO convertToResponseDTO(CostCenter costCenter) {
        return CostCenterResponseDTO.builder()
                .id(costCenter.getId())
                .code(costCenter.getCode())
                .name(costCenter.getName())
                .description(costCenter.getDescription())
                .isActive(costCenter.getIsActive())
                .createdAt(costCenter.getCreatedAt())
                .updatedAt(costCenter.getUpdatedAt())
                .build();
    }
}
