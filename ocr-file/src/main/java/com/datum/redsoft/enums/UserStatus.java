package com.datum.redsoft.enums;

/**
 * Enum para los estados de usuario en el sistema
 * Define los diferentes estados que puede tener un usuario
 */
public enum UserStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    SUSPENDED("Suspendido");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Obtiene el nombre para mostrar del estado
     * @return Nombre amigable del estado
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}