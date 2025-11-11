package com.datum.redsoft.service;

import com.datum.redsoft.config.HuggingFaceConfig;
import com.datum.redsoft.dto.response.InvoiceDataResponse;
import com.datum.redsoft.dto.response.OCRResponseDTO;
import com.datum.redsoft.exception.InvoiceExtractionException;
import com.datum.redsoft.model.llama.LlamaRequest;
import com.datum.redsoft.model.llama.LlamaMessage;
import com.datum.redsoft.model.llama.LlamaResponse;
import com.datum.redsoft.service.interfaces.InvoiceDataExtractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementación del servicio de extracción de datos de facturas usando modelos LLaMA.
 * <p>Utiliza la API de Hugging Face Router para procesar texto extraído por OCR
 * y extraer campos estructurados de facturas mediante inteligencia artificial.</p>
 * <p>Sigue el principio de <b>Responsabilidad Única (SRP)</b>: solo se encarga
 * de extraer y estructurar datos de facturas usando IA.</p>
 * 
 * @author Datum Redsoft
 * @version 1.0
 */
@ApplicationScoped
public class LlamaInvoiceExtractionService implements InvoiceDataExtractionService {
    
    private static final Logger logger = Logger.getLogger(LlamaInvoiceExtractionService.class.getName());
    
    @Inject
    HuggingFaceConfig hfConfig;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public InvoiceDataResponse extractInvoiceData(String extractedText) throws InvoiceExtractionException {
        try {
            logger.info("Iniciando extracción de datos de factura con Llama AI");
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new InvoiceExtractionException("El texto extraído está vacío o es nulo");
            }
            
            String prompt = createInvoiceExtractionPrompt(extractedText);
            LlamaRequest request = createLlamaRequest(prompt);
            String jsonResponse = makeHttpRequestWithRetry(request);
            
            InvoiceDataResponse response = parseInvoiceResponse(jsonResponse);
            response.setExtractionMethod("AI");
            response.setConfidenceScore(0.85);
            
            logger.info("Extracción AI completada exitosamente");
            return response;
            
        } catch (Exception e) {
            logger.severe("Error en extracción AI: " + e.getMessage());
            throw new InvoiceExtractionException("Error al extraer datos de la factura", e);
        }
    }

    @Override
    public OCRResponseDTO extractBasicInvoiceData(String extractedText) throws InvoiceExtractionException {
        try {
            logger.info("Iniciando extracción básica de datos con Llama AI");
            
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new InvoiceExtractionException("El texto extraído está vacío o es nulo");
            }
            
            String prompt = createInvoiceExtractionPrompt(extractedText);
            LlamaRequest request = createLlamaRequest(prompt);
            String jsonResponse = makeHttpRequestWithRetry(request);
            OCRResponseDTO response = parseBasicInvoiceResponse(jsonResponse);
            
            logger.info("Extracción básica AI completada exitosamente");
            return response;
            
        } catch (Exception e) {
            logger.severe("Error en extracción básica AI: " + e.getMessage());
            throw new InvoiceExtractionException("Error al extraer datos básicos de la factura", e);
        }
    }
    


    private String createInvoiceExtractionPrompt(String ocrText) {
        return String.format("""
            You are an AI assistant that extracts information from invoices and receipts.
            
            Extract ONLY the following 4 fields from this invoice/receipt text and return a valid JSON object:
            
            Text: %s
            
            Extract these fields:
            - vendor_name: Name of the business/company that issued the invoice
            - invoice_date: Date of the invoice (format YYYY-MM-DD)
            - total_amount: Total amount (numbers only, no currency symbols)
            - currency: Currency used (USD, EUR, MXN, PEN, etc.)
            
            Return ONLY this JSON format, no other text:
            {"vendor_name":"...","invoice_date":"...","total_amount":"...","currency":"..."}
            
            Use "Not found" for missing information.
            """, ocrText.replace("\"", "\\\""));
    }

    private LlamaRequest createLlamaRequest(String prompt) {
        LlamaMessage userMessage = new LlamaMessage("user", prompt);
        
        return new LlamaRequest(
            Arrays.asList(userMessage),
            hfConfig.getModel(),
            false,
            hfConfig.getMaxTokens(),
            hfConfig.getTemperature()
        );
    }

    /**
     * Realiza petición HTTP a Hugging Face con estrategia de reintentos automáticos.
     * <p>Implementa backoff exponencial: el tiempo de espera aumenta con cada intento.</p>
     * 
     * @param request Request configurado para Llama
     * @return Respuesta JSON del servicio de IA
     * @throws InvoiceExtractionException si todos los reintentos fallan
     */
    private String makeHttpRequestWithRetry(LlamaRequest request) throws Exception {
        int maxRetries = hfConfig.getMaxRetryAttempts();
        long delayMs = hfConfig.getRetryDelayMs();
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return makeHttpRequest(request);
            } catch (Exception e) {
                lastException = e;
                logger.warning(String.format("Intento %d/%d falló: %s", attempt, maxRetries, e.getMessage()));
                
                if (attempt < maxRetries) {
                    Thread.sleep(delayMs * attempt);
                }
            }
        }
        
        throw new InvoiceExtractionException("Falló después de " + maxRetries + " intentos", lastException);
    }
    
    /**
     * Realiza una petición HTTP individual a la API de Hugging Face.
     * 
     * @param request Request configurado para Llama
     * @return Respuesta JSON del servicio
     * @throws RuntimeException si el status code no es 200
     */
    private String makeHttpRequest(LlamaRequest request) throws Exception {
        logger.info("Enviando petición a Hugging Face Router API");
        
        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(hfConfig.getApiUrl()))
                .header("Authorization", "Bearer " + hfConfig.getToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en API de Hugging Face: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    private InvoiceDataResponse parseInvoiceResponse(String jsonResponse) throws Exception {
        logger.info("Parseando respuesta de Llama Router");
        
        LlamaResponse llamaResponse = objectMapper.readValue(jsonResponse, LlamaResponse.class);
        
        if (llamaResponse.getChoices() != null && !llamaResponse.getChoices().isEmpty()) {
            String content = llamaResponse.getChoices().get(0).getMessage().getContent();
            String jsonContent = extractJsonFromResponse(content);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> dataMap = objectMapper.readValue(jsonContent, java.util.Map.class);
            
            return new InvoiceDataResponse(
                (String) dataMap.getOrDefault("company_name", "No encontrado"),
                (String) dataMap.getOrDefault("invoice_date", "No encontrado"),
                (String) dataMap.getOrDefault("total_amount", "0"),
                (String) dataMap.getOrDefault("currency", "No encontrado"),
                (String) dataMap.getOrDefault("invoice_number", "No encontrado"),
                "AI",
                0.85
            );
        }
        
        throw new RuntimeException("No se recibió respuesta válida de Llama");
    }

    private OCRResponseDTO parseBasicInvoiceResponse(String jsonResponse) throws Exception {
        logger.info("Parseando respuesta básica de Llama Router");
        
        LlamaResponse llamaResponse = objectMapper.readValue(jsonResponse, LlamaResponse.class);
        
        if (llamaResponse.getChoices() != null && !llamaResponse.getChoices().isEmpty()) {
            String content = llamaResponse.getChoices().get(0).getMessage().getContent();
            String jsonContent = extractBasicJsonFromResponse(content);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> dataMap = objectMapper.readValue(jsonContent, java.util.Map.class);
            
            return new OCRResponseDTO(
                (String) dataMap.getOrDefault("vendor_name", "No encontrado"),
                (String) dataMap.getOrDefault("invoice_date", "No encontrado"),
                (String) dataMap.getOrDefault("total_amount", "0"),
                (String) dataMap.getOrDefault("currency", "No encontrado")
            );
        }
        
        throw new RuntimeException("No se recibió respuesta válida de Llama para datos básicos");
    }

    private String extractBasicJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return """
            {
                "vendor_name": "Error al procesar",
                "invoice_date": "No encontrado",
                "total_amount": "0",
                "currency": "No encontrado"
            }
            """;
    }

    private String extractJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return """
            {
                "company_name": "Error al procesar",
                "invoice_date": "No encontrado",
                "total_amount": "0",
                "currency": "No encontrado",
                "invoice_number": "No encontrado"
            }
            """;
    }

    @Override
    public boolean isServiceAvailable() {
        return hfConfig.isValid();
    }

    @Override
    public String getExtractionMethod() {
        return "AI";
    }
}