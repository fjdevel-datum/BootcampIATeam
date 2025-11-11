package com.datum.redsoft.enums;

/**
 * Enum para los roles de usuario en el sistema
 * Define los diferentes tipos de usuarios que pueden acceder al sistema
 */
public enum UserRole {
    COLLABORATOR("Colaborador"),
    ADMIN("Administrador");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Obtiene el nombre para mostrar del rol
     * @return Nombre amigable del rol
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}