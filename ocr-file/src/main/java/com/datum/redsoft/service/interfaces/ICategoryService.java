package com.datum.redsoft.service.interfaces;

import com.datum.redsoft.dto.request.CreateCategoryRequest;
import com.datum.redsoft.dto.request.UpdateCategoryRequest;
import com.datum.redsoft.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de categorías
 * Define los contratos para la gestión de categorías
 */
public interface ICategoryService {
    
    /**
     * Obtiene todas las categorías
     */
    List<CategoryResponseDTO> getAllCategories();
    
    /**
     * Obtiene una categoría por ID
     */
    Optional<CategoryResponseDTO> getCategoryById(Long id);
    
    /**
     * Obtiene una categoría por nombre
     */
    Optional<CategoryResponseDTO> getCategoryByName(String name);
    
    /**
     * Obtiene solo las categorías activas
     */
    List<CategoryResponseDTO> getActiveCategories();
    
    /**
     * Crea una nueva categoría
     */
    CategoryResponseDTO createCategory(CreateCategoryRequest request);
    
    /**
     * Actualiza una categoría existente
     */
    Optional<CategoryResponseDTO> updateCategory(Long id, UpdateCategoryRequest request);
    
    /**
     * Elimina una categoría
     */
    boolean deleteCategory(Long id);
    
    /**
     * Activa una categoría
     */
    boolean activateCategory(Long id);
    
    /**
     * Desactiva una categoría
     */
    boolean deactivateCategory(Long id);
}
