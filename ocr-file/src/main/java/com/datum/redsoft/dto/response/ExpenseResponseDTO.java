package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para gastos individuales asociados a una tarjeta.
 * <p>Contiene información completa de un gasto incluyendo datos de la factura,
 * categorización, centro de costos y archivo adjunto.</p>
 * 
 * @author Datum Redsoft
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDTO {
    
    private Long id;
    private Long idInvoice;
    private String vendorName;
    private String concept;
    private String category;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private String currency;
    private Long categoryId;
    private Long costCenterId;
    private String costCenterName;
    private String clientVisited;
    private String notes;
    private String status;
    private Long countryId;
    private String path;
    private String fileName;
    private String icon;
    public ExpenseResponseDTO(Long id, Long idInvoice, String vendorName, String concept, 
                            String category, LocalDate invoiceDate, BigDecimal totalAmount, 
                            String currency, Long categoryId, Long costCenterId, 
                            String costCenterName, String clientVisited, String notes, 
                            String status, Long countryId, String path, String fileName) {
        this.id = id;
        this.idInvoice = idInvoice;
        this.vendorName = vendorName;
        this.concept = concept;
        this.category = category;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.categoryId = categoryId;
        this.costCenterId = costCenterId;
        this.costCenterName = costCenterName;
        this.clientVisited = clientVisited;
        this.notes = notes;
        this.status = status;
        this.countryId = countryId;
        this.path = path;
        this.fileName = fileName;
        this.icon = "cash";
    }
}