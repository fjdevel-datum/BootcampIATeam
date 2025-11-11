package com.datum.redsoft.service;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.datum.redsoft.exception.OCRException;
import com.datum.redsoft.service.interfaces.OCRService;
import com.datum.redsoft.config.AzureConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Base64;
import java.util.logging.Logger;

/**
 * Implementación del servicio OCR usando Azure Document Intelligence.
 * <p>Extrae texto de imágenes y documentos PDF utilizando el servicio Azure
 * Document Intelligence (anteriormente Azure Form Recognizer).</p>
 * <p>Sigue el principio de <b>Responsabilidad Única (SRP)</b>: solo se encarga
 * de la extracción de texto mediante OCR.</p>
 * 
 * @author Datum Redsoft
 * @version 1.0
 */
@ApplicationScoped
public class AzureOCRService implements OCRService {
    
    private static final Logger logger = Logger.getLogger(AzureOCRService.class.getName());
    
    @Inject
    AzureConfig azureConfig;
    
    private DocumentIntelligenceClient client;
    
    /**
     * Inicializa el cliente de Azure Document Intelligence de forma lazy.
     * <p>El cliente solo se crea la primera vez que se necesita y luego se reutiliza.</p>
     */
    private void initializeClient() {
        if (client == null) {
            logger.info("Inicializando cliente de Azure Document Intelligence");
            client = new DocumentIntelligenceClientBuilder()
                    .credential(new AzureKeyCredential(azureConfig.getApiKey()))
                    .endpoint(azureConfig.getEndpoint())
                    .buildClient();
        }
    }
    
    @Override
    public String extractTextFromImage(byte[] imageData) throws OCRException {
        try {
            initializeClient();
            logger.info("Iniciando extracción de texto con Azure OCR");
            
            String base64Data = Base64.getEncoder().encodeToString(imageData);
            String requestBody = String.format("{\"base64Source\": \"%s\"}", base64Data);
            
            var poller = client.beginAnalyzeDocument(azureConfig.getModel(), BinaryData.fromString(requestBody), null);
            BinaryData resultData = poller.getFinalResult();
            AnalyzeResult analyzeResult = resultData.toObject(AnalyzeResult.class);
            
            StringBuilder extractedText = new StringBuilder();
            
            if (analyzeResult.getContent() != null) {
                extractedText.append(analyzeResult.getContent());
            }
            
            // Estrategia de fallback: si no hay contenido directo, extraer por páginas y líneas
            if (extractedText.length() == 0 && analyzeResult.getPages() != null) {
                analyzeResult.getPages().forEach(page -> {
                    if (page.getLines() != null) {
                        page.getLines().forEach(line -> {
                            extractedText.append(line.getContent()).append("\n");
                        });
                    }
                });
            }
            
            String text = extractedText.toString().trim();
            logger.info("Texto extraído exitosamente. Longitud: " + text.length());
            
            if (text.isEmpty()) {
                throw new OCRException("No se pudo extraer texto de la imagen");
            }
            
            return text;
            
        } catch (Exception e) {
            logger.severe("Error en extracción OCR: " + e.getMessage());
            throw new OCRException("Error al procesar la imagen con Azure OCR", e);
        }
    }
    
    @Override
    public boolean isServiceAvailable() {
        try {
            return azureConfig.isValid();
        } catch (Exception e) {
            logger.warning("Servicio Azure OCR no disponible: " + e.getMessage());
            return false;
        }
    }
}