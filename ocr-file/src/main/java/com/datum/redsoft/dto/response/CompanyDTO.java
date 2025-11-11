package com.datum.redsoft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Company
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private CountryDTO country;
    private String address;
}