package com.datum.redsoft.service;

import com.datum.redsoft.dto.request.CreateCategoryRequest;
import com.datum.redsoft.dto.request.UpdateCategoryRequest;
import com.datum.redsoft.dto.response.CategoryResponseDTO;
import com.datum.redsoft.entity.Category;
import com.datum.redsoft.repository.CategoryRepository;
import com.datum.redsoft.service.interfaces.ICategoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de categorías
 * Maneja toda la lógica de negocio relacionada con las categorías
 */
@ApplicationScoped
public class CategoryService implements ICategoryService {
    
    private static final Logger logger = Logger.getLogger(CategoryService.class.getName());
    
    @Inject
    CategoryRepository categoryRepository;
    
    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        logger.info("Obteniendo todas las categorías");
        return categoryRepository.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        logger.info("Buscando categoría con ID: " + id);
        return categoryRepository.findByIdOptional(id)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public Optional<CategoryResponseDTO> getCategoryByName(String name) {
        logger.info("Buscando categoría con nombre: " + name);
        return categoryRepository.findByName(name)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    public List<CategoryResponseDTO> getActiveCategories() {
        logger.info("Obteniendo categorías activas");
        return categoryRepository.findActiveCategories().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequest request) {
        logger.info("Creando nueva categoría: " + request.getName());
        
        // Validar que no exista una categoría con el mismo nombre
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getName());
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        categoryRepository.persist(category);
        logger.info("Categoría creada exitosamente con ID: " + category.getId());
        
        return convertToResponseDTO(category);
    }
    
    @Override
    @Transactional
    public Optional<CategoryResponseDTO> updateCategory(Long id, UpdateCategoryRequest request) {
        logger.info("Actualizando categoría con ID: " + id);
        
        Optional<Category> categoryOpt = categoryRepository.findByIdOptional(id);
        if (categoryOpt.isEmpty()) {
            logger.warning("Categoría no encontrada con ID: " + id);
            return Optional.empty();
        }
        
        Category category = categoryOpt.get();
        
        // Actualizar campos no nulos
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            // Validar que el nuevo nombre no exista
            if (categoryRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getName());
            }
            category.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.merge(category);
        
        logger.info("Categoría actualizada exitosamente con ID: " + id);
        return Optional.of(convertToResponseDTO(category));
    }
    
    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        logger.info("Eliminando categoría con ID: " + id);
        
        Optional<Category> categoryOpt = categoryRepository.findByIdOptional(id);
        if (categoryOpt.isEmpty()) {
            logger.warning("Categoría no encontrada con ID: " + id);
            return false;
        }
        
        categoryRepository.remove(categoryOpt.get());
        logger.info("Categoría eliminada exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean activateCategory(Long id) {
        logger.info("Activando categoría con ID: " + id);
        
        Optional<Category> categoryOpt = categoryRepository.findByIdOptional(id);
        if (categoryOpt.isEmpty()) {
            logger.warning("Categoría no encontrada con ID: " + id);
            return false;
        }
        
        Category category = categoryOpt.get();
        category.setIsActive(true);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.merge(category);
        
        logger.info("Categoría activada exitosamente");
        return true;
    }
    
    @Override
    @Transactional
    public boolean deactivateCategory(Long id) {
        logger.info("Desactivando categoría con ID: " + id);
        
        Optional<Category> categoryOpt = categoryRepository.findByIdOptional(id);
        if (categoryOpt.isEmpty()) {
            logger.warning("Categoría no encontrada con ID: " + id);
            return false;
        }
        
        Category category = categoryOpt.get();
        category.setIsActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.merge(category);
        
        logger.info("Categoría desactivada exitosamente");
        return true;
    }
    
    /**
     * Convierte una entidad Category a CategoryResponseDTO
     */
    private CategoryResponseDTO convertToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
