package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para representar un grupo de gastos agrupados por mes-año
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseGroupResponseDTO {
    
    private String month;              // Mes y año (ej: "Diciembre 2024")
    private BigDecimal total;          // Total calculado del mes
    private Integer count;             // Cantidad de facturas en el mes
    private String status;             // Status del grupo ("PENDIENTE", "APROBADO", "MIXTO")
    private List<ExpenseResponseDTO> expenses;  // Lista de gastos del mes
}