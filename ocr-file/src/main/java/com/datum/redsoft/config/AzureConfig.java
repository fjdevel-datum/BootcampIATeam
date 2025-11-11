package com.datum.redsoft.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Configuración para el servicio Azure Document Intelligence
 * Centraliza la configuración siguiendo el principio de Single Responsibility
 */
@ApplicationScoped
public class AzureConfig {
    
    @ConfigProperty(name = "azure.document-intelligence.endpoint")
    private String endpoint;
    
    @ConfigProperty(name = "azure.document-intelligence.key")
    private String apiKey;
    
    @ConfigProperty(name = "azure.document-intelligence.model", 
                   defaultValue = "prebuilt-read")
    private String model;
    
    @ConfigProperty(name = "azure.document-intelligence.timeout", 
                   defaultValue = "30")
    private Integer timeoutSeconds;
    
    @ConfigProperty(name = "azure.document-intelligence.retry.max-attempts", 
                   defaultValue = "3")
    private Integer maxRetryAttempts;
    
    @ConfigProperty(name = "azure.document-intelligence.retry.delay-ms", 
                   defaultValue = "1000")
    private Long retryDelayMs;

    // Getters
    public String getEndpoint() {
        return endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public Integer getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public Long getRetryDelayMs() {
        return retryDelayMs;
    }
    
    /**
     * Valida que la configuración sea válida
     */
    public boolean isValid() {
        return endpoint != null && !endpoint.isEmpty() &&
               apiKey != null && !apiKey.isEmpty() &&
               model != null && !model.isEmpty() &&
               timeoutSeconds > 0 && maxRetryAttempts > 0;
    }

    @Override
    public String toString() {
        return "AzureConfig{" +
                "endpoint='" + endpoint + '\'' +
                ", apiKey='***HIDDEN***'" +
                ", model='" + model + '\'' +
                ", timeoutSeconds=" + timeoutSeconds +
                ", maxRetryAttempts=" + maxRetryAttempts +
                ", retryDelayMs=" + retryDelayMs +
                '}';
    }
}