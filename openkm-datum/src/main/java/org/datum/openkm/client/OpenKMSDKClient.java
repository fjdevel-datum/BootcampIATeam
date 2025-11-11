package org.datum.openkm.client;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.datum.openkm.config.OpenKMConfig;
import org.datum.openkm.dto.DownloadedDocument;
import org.datum.openkm.dto.OpenKMDocument;
import org.datum.openkm.exception.OpenKMException;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente HTTP para interactuar con la API REST de OpenKM.
 * Implementación usando Apache HttpClient 5.
 */
@ApplicationScoped
public class OpenKMSDKClient {

    private static final Logger LOG = Logger.getLogger(OpenKMSDKClient.class);

    @Inject
    OpenKMConfig config;

    private CloseableHttpClient httpClient;

    /**
     * Inicializa el cliente HTTP.
     */
    @PostConstruct
    public void init() {
        LOG.infof("Inicializando OpenKM HTTP Client");
        LOG.infof("URL: %s", config.url());
        LOG.infof("Usuario: %s", config.username());

        this.httpClient = HttpClients.createDefault();

        LOG.info("✓ OpenKM HTTP Client inicializado correctamente");
    }

    /**
     * Cierra el cliente HTTP al destruir el bean.
     */
    @PreDestroy
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
                LOG.info("OpenKM HTTP Client cerrado");
            } catch (IOException e) {
                LOG.warn("Error al cerrar HTTP Client", e);
            }
        }
    }

    /**
     * Sube un documento a OpenKM usando multipart/form-data.
     *
     * @param docPath Ruta completa donde se guardará el documento (ej: /okm:root/images/test.jpg)
     * @param content Contenido del documento como array de bytes
     * @param mimeType Tipo MIME del documento
     * @return Objeto OpenKMDocument con los metadatos del documento creado
     * @throws OpenKMException si ocurre un error durante la subida
     */
    public OpenKMDocument uploadDocument(String docPath, byte[] content, String mimeType) {
        String url = buildUrl("/services/rest/document/createSimple");
        
        LOG.infof("=== Subiendo documento con HTTP Client ===");
        LOG.infof("URL: %s", url);
        LOG.infof("Ruta: %s", docPath);
        LOG.infof("MIME Type: %s", mimeType);
        LOG.infof("Tamaño: %d bytes", content.length);

        HttpPost httpPost = new HttpPost(url);
        
        try {
            // Agregar autenticación Basic
            String auth = getBasicAuthHeader();
            httpPost.setHeader("Authorization", auth);

            // Construir entidad multipart
            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("content", content, ContentType.create(mimeType), extractFileName(docPath))
                    .addTextBody("docPath", docPath, ContentType.TEXT_PLAIN)
                    .build();

            httpPost.setEntity(multipartEntity);

            LOG.debugf("Enviando petición POST...");

            // Ejecutar petición
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                LOG.infof("Respuesta HTTP: %d", statusCode);
                LOG.debugf("Cuerpo de respuesta: %s", responseBody);

                if (statusCode >= 200 && statusCode < 300) {
                    LOG.infof("Documento creado exitosamente");
                    // Parsear la respuesta XML para extraer los metadatos
                    return parseXmlResponse(responseBody.trim());
                } else {
                    LOG.errorf("Error HTTP %d: %s", statusCode, responseBody);
                    throw new OpenKMException(
                            String.format("Error al subir documento: HTTP %d - %s", statusCode, responseBody),
                            statusCode
                    );
                }
            }

        } catch (IOException | ParseException e) {
            LOG.errorf("Error de I/O: %s", e.getMessage());
            throw new OpenKMException(
                    "Error de conexión con OpenKM: " + e.getMessage(),
                    500,
                    e
            );
        }
    }

    /**
     * Verifica la conectividad con OpenKM.
     *
     * @return true si la conexión es exitosa
     */
    public boolean testConnection() {
        try {
            LOG.debug("Probando conexión con OpenKM");
            
            // Intentar una petición simple
            String url = buildUrl("/services/rest/repository/getRootFolder");
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", getBasicAuthHeader());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                boolean success = statusCode >= 200 && statusCode < 300;
                
                if (success) {
                    LOG.debug("Conexión exitosa");
                } else {
                    LOG.warnf("Conexión falló con código: %d", statusCode);
                }
                
                return success;
            }
        } catch (Exception e) {
            LOG.errorf("✗ Error de conexión: %s", e.getMessage());
            return false;
        }
    }

    /**
     * Construye la URL completa para un endpoint.
     *
     * @param endpoint Endpoint de la API
     * @return URL completa
     */
    private String buildUrl(String endpoint) {
        String baseUrl = config.url();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        return baseUrl + endpoint;
    }

    /**
     * Genera el header de autenticación Basic.
     *
     * @return Header de autenticación
     */
    private String getBasicAuthHeader() {
        String credentials = config.username() + ":" + config.password();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    /**
     * Extrae el nombre del archivo de una ruta completa.
     *
     * @param docPath Ruta completa del documento
     * @return Nombre del archivo
     */
    private String extractFileName(String docPath) {
        int lastSlash = docPath.lastIndexOf('/');
        return lastSlash >= 0 ? docPath.substring(lastSlash + 1) : docPath;
    }

    /**
     * Parsea la respuesta XML de OpenKM y extrae los metadatos del documento.
     * Ejemplo de XML esperado:
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <document>
     *   <uuid>2a3232fc-f817-4d2b-8927-c314afaabba6</uuid>
     *   <path>/okm:root/factura</path>
     *   <author>okmAdmin</author>
     *   <mimeType>image/jpeg</mimeType>
     *   <size>123456</size>
     *   <created>2024-01-15T10:30:00</created>
     *   <checksum>abc123...</checksum>
     * </document>
     * }
     * </pre>
     *
     * @param xml Respuesta XML de OpenKM
     * @return Objeto OpenKMDocument con los datos parseados
     * @throws OpenKMException si el XML no puede ser parseado
     */
    private OpenKMDocument parseXmlResponse(String xml) {
        try {
            LOG.debugf("Parseando respuesta XML: %s", xml);

            // Usar expresiones regulares para extraer los valores
            String uuid = extractXmlValue(xml, "uuid");
            String path = extractXmlValue(xml, "path");
            String author = extractXmlValue(xml, "author");
            String mimeType = extractXmlValue(xml, "mimeType");
            String sizeStr = extractXmlValue(xml, "size");
            String createdStr = extractXmlValue(xml, "created");
            String checksum = extractXmlValue(xml, "checksum");
            String lockedStr = extractXmlValue(xml, "locked");
            String convertibleStr = extractXmlValue(xml, "convertibleToPdf");

            // Convertir size a Long
            Long size = null;
            if (sizeStr != null && !sizeStr.isEmpty()) {
                try {
                    size = Long.parseLong(sizeStr);
                } catch (NumberFormatException e) {
                    LOG.warnf("No se pudo parsear size: %s", sizeStr);
                }
            }

            // Convertir created a LocalDateTime
            LocalDateTime created = null;
            if (createdStr != null && !createdStr.isEmpty()) {
                try {
                    // Formato típico de OpenKM: 2024-01-15T10:30:00.000-05:00
                    // Manejar formato con milisegundos y timezone
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    created = LocalDateTime.parse(createdStr, formatter);
                } catch (DateTimeParseException e) {
                    LOG.warnf("No se pudo parsear created: %s", createdStr);
                }
            }

            // Convertir boolean strings
            Boolean locked = lockedStr != null ? Boolean.parseBoolean(lockedStr) : false;
            Boolean convertibleToPdf = convertibleStr != null ? Boolean.parseBoolean(convertibleStr) : false;

            OpenKMDocument document = OpenKMDocument.builder()
                    .uuid(uuid)
                    .path(path)
                    .author(author)
                    .mimeType(mimeType)
                    .size(size)
                    .created(created)
                    .checksum(checksum)
                    .locked(locked)
                    .convertibleToPdf(convertibleToPdf)
                    .build();

            LOG.infof("Documento parseado exitosamente - UUID: %s", uuid);
            return document;

        } catch (Exception e) {
            LOG.errorf("Error al parsear respuesta XML: %s", e.getMessage());
            throw new OpenKMException("Error al parsear respuesta XML: " + e.getMessage(), 500, e);
        }
    }

    /**
     * Extrae el valor de un tag XML usando expresión regular.
     *
     * @param xml Contenido XML
     * @param tagName Nombre del tag a extraer
     * @return Valor del tag, o null si no se encuentra
     */
    private String extractXmlValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Descarga un documento desde OpenKM.
     *
     * @param docPath Ruta completa del documento en OpenKM (ej: /okm:root/images/foto.jpg)
     * @return DownloadedDocument con el contenido del archivo y su tipo MIME
     * @throws OpenKMException si ocurre un error durante la descarga
     */
    public DownloadedDocument downloadDocument(String docPath) {
        String url = buildUrl("/Download");
        
        LOG.infof("=== Descargando documento de OpenKM ===");
        LOG.infof("URL: %s", url);
        LOG.infof("Ruta: %s", docPath);

        HttpGet httpGet = new HttpGet(url + "?path=" + encodeUrlParameter(docPath));
        
        try {
            // Agregar autenticación Basic
            String auth = getBasicAuthHeader();
            httpGet.setHeader("Authorization", auth);

            LOG.debugf("Enviando petición GET...");

            // Ejecutar petición
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getCode();

                LOG.infof("Respuesta HTTP: %d", statusCode);

                if (statusCode == 404) {
                    LOG.errorf("Documento no encontrado: %s", docPath);
                    throw new OpenKMException(
                            String.format("Documento no encontrado: %s", docPath),
                            404
                    );
                } else if (statusCode >= 200 && statusCode < 300) {
                    // Obtener el tipo MIME de la respuesta desde el header Content-Type
                    String contentType = "application/octet-stream"; // Valor por defecto
                    
                    // Intentar obtener el Content-Type del header de la respuesta
                    if (response.getFirstHeader("Content-Type") != null) {
                        contentType = response.getFirstHeader("Content-Type").getValue();
                        // Limpiar el content-type (remover charset si existe)
                        if (contentType.contains(";")) {
                            contentType = contentType.split(";")[0].trim();
                        }
                    }

                    // Leer el contenido del documento
                    byte[] content = EntityUtils.toByteArray(response.getEntity());

                    LOG.infof("Documento descargado exitosamente");
                    LOG.infof("Content-Type: %s", contentType);
                    LOG.infof("Tamaño: %d bytes", content.length);

                    return new DownloadedDocument(content, contentType);
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    LOG.errorf("Error HTTP %d: %s", statusCode, responseBody);
                    throw new OpenKMException(
                            String.format("Error al descargar documento: HTTP %d - %s", statusCode, responseBody),
                            statusCode
                    );
                }
            }

        } catch (OpenKMException e) {
            throw e;
        } catch (IOException | ParseException e) {
            LOG.errorf("Error de I/O: %s", e.getMessage());
            throw new OpenKMException(
                    "Error de conexión con OpenKM: " + e.getMessage(),
                    500,
                    e
            );
        }
    }

    /**
     * Codifica un parámetro de URL.
     *
     * @param value Valor a codificar
     * @return Valor codificado
     */
    private String encodeUrlParameter(String value) {
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.warnf("Error al codificar parámetro: %s", e.getMessage());
            return value;
        }
    }
}
