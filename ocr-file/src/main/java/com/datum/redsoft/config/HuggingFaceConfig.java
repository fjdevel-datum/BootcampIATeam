package com.datum.redsoft.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Configuración para el servicio Hugging Face
 * Centraliza la configuración siguiendo el principio de Single Responsibility
 */
@ApplicationScoped
public class HuggingFaceConfig {
    
    @ConfigProperty(name = "huggingface.token")
    private String token;

    @ConfigProperty(name = "huggingface.api.url", 
                   defaultValue = "https://router.huggingface.co/v1/chat/completions")
    private String apiUrl;

    @ConfigProperty(name = "huggingface.model", 
                   defaultValue = "meta-llama/Llama-3.1-8B-Instruct:cerebras")
    private String model;
    
    @ConfigProperty(name = "huggingface.max-tokens", 
                   defaultValue = "1000")
    private Integer maxTokens;
    
    @ConfigProperty(name = "huggingface.temperature", 
                   defaultValue = "0.3")
    private Double temperature;
    
    @ConfigProperty(name = "huggingface.timeout-seconds", 
                   defaultValue = "30")
    private Integer timeoutSeconds;
    
    @ConfigProperty(name = "huggingface.retry.max-attempts", 
                   defaultValue = "3")
    private Integer maxRetryAttempts;
    
    @ConfigProperty(name = "huggingface.retry.delay-ms", 
                   defaultValue = "1000")
    private Long retryDelayMs;

    // Getters
    public String getToken() {
        return token;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getModel() {
        return model;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Double getTemperature() {
        return temperature;
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
        return token != null && !token.isEmpty() &&
               apiUrl != null && !apiUrl.isEmpty() &&
               model != null && !model.isEmpty() &&
               maxTokens > 0 && temperature >= 0.0 && temperature <= 1.0 &&
               timeoutSeconds > 0 && maxRetryAttempts > 0;
    }

    @Override
    public String toString() {
        return "HuggingFaceConfig{" +
                "token='***HIDDEN***'" +
                ", apiUrl='" + apiUrl + '\'' +
                ", model='" + model + '\'' +
                ", maxTokens=" + maxTokens +
                ", temperature=" + temperature +
                ", timeoutSeconds=" + timeoutSeconds +
                ", maxRetryAttempts=" + maxRetryAttempts +
                ", retryDelayMs=" + retryDelayMs +
                '}';
    }
}