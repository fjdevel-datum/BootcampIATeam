package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Country
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {
    private Long id;
    private String isoCode;
    private String name;
}