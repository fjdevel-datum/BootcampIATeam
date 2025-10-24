# Funcionalidad de Subida de Documentos Excel

## Descripción General

Esta funcionalidad permite subir documentos de Microsoft Excel (.xlsx y .xls) a OpenKM utilizando una API REST. La implementación sigue la misma arquitectura que la funcionalidad de subida de imágenes, reutilizando componentes existentes y manteniendo la consistencia del proyecto.

## Componentes Implementados

### 1. DTO: ExcelUploadRequest.java

**Ubicación:** `src/main/java/org/datum/openkm/dto/ExcelUploadRequest.java`

**Propósito:** Encapsular todos los datos necesarios para la subida de un documento Excel.

**Campos:**
- `fileName` (String, requerido): Nombre del archivo en OpenKM
- `destinationPath` (String, requerido): Ruta de destino en OpenKM
- `documentData` (byte[], requerido): Datos del documento en formato byte array
- `description` (String, opcional): Descripción del documento
- `mimeType` (String, opcional): Tipo MIME del documento (valor por defecto: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`)

**Características:**
- Validaciones con Jakarta Bean Validation (@NotBlank, @NotNull)
- Documentación OpenAPI con @Schema
- Patrón Builder para construcción fluida
- Valor por defecto para mimeType (Excel 2007+)

### 2. Servicio: ImageUploadService.java (Actualizado)

**Ubicación:** `src/main/java/org/datum/openkm/service/ImageUploadService.java`

**Nuevos métodos añadidos:**

#### `uploadExcelDocument(ExcelUploadRequest request)`
- **Propósito:** Subir un documento Excel a OpenKM
- **Retorno:** `ImageUploadResponse` con información del documento creado
- **Validaciones:**
  - Datos del documento no vacíos
  - Tamaño máximo de 50MB
  - Tipo MIME válido (.xlsx o .xls)

#### `validateExcelDocument(ExcelUploadRequest request)`
- **Propósito:** Validar los datos del documento antes de subirlo
- **Validaciones realizadas:**
  - Verificación de datos no nulos/vacíos
  - Control de tamaño máximo (50MB)
  - Validación de tipos MIME permitidos

#### `isValidExcelMimeType(String mimeType)`
- **Propósito:** Verificar si el tipo MIME corresponde a un formato Excel válido
- **Tipos MIME soportados:**
  - `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (.xlsx)
  - `application/vnd.ms-excel` (.xls)

### 3. Controlador: ImageUploadController.java (Actualizado)

**Ubicación:** `src/main/java/org/datum/openkm/controller/ImageUploadController.java`

**Nuevo endpoint añadido:**

#### `POST /api/images/upload/excel`

**Propósito:** Subir un documento Excel a OpenKM usando multipart/form-data

**Parámetros:**
- `file` (FileUpload, requerido): Archivo Excel a subir
- `fileName` (String, requerido): Nombre del archivo en OpenKM
- `destinationPath` (String, opcional): Ruta de destino (por defecto: `/okm:root/documentos/excel`)
- `description` (String, opcional): Descripción del documento

**Respuestas:**
- `201 Created`: Documento subido exitosamente
- `400 Bad Request`: Error de validación o archivo inválido
- `500 Internal Server Error`: Error del servidor o comunicación con OpenKM

**Características:**
- Documentación completa con OpenAPI (@Operation, @APIResponses, @Parameter)
- Manejo robusto de excepciones con try-catch
- Logging detallado de operaciones
- Validación de archivo nulo
- Detección automática de MIME type si no se proporciona

## Arquitectura y Reutilización

### Componentes Reutilizados

1. **OpenKMSDKClient**: Cliente HTTP que se utiliza para comunicarse con OpenKM
   - Método `uploadDocument()` es genérico y funciona para cualquier tipo de documento

2. **ImageUploadResponse**: DTO de respuesta reutilizado
   - Aunque el nombre sugiere "Image", la estructura es genérica y sirve para cualquier documento
   - Contiene: documentId, fileName, path, size, mimeType, uploadDate, message, success

3. **ImageUploadException**: Excepción personalizada reutilizada
   - Manejo de errores consistente en toda la aplicación

4. **GlobalExceptionHandler**: Manejador global de excepciones
   - Proporciona respuestas de error uniformes

### Flujo de Datos

```
1. Cliente → POST /api/images/upload/excel (multipart/form-data)
   ↓
2. ImageUploadController.uploadExcel()
   - Valida que el archivo no sea nulo
   - Lee los bytes del archivo
   - Construye ExcelUploadRequest
   ↓
3. ImageUploadService.uploadExcelDocument()
   - Valida el documento (tamaño, tipo MIME)
   - Construye la ruta completa
   - Registra logs detallados
   ↓
4. OpenKMSDKClient.uploadDocument()
   - Crea petición HTTP multipart
   - Añade autenticación Basic
   - Envía a OpenKM REST API
   - Parsea respuesta XML
   ↓
5. Respuesta ← ImageUploadResponse
   - documentId, path, size, etc.
```

## Uso

### Ejemplo con cURL

```bash
curl -X POST http://localhost:8080/api/images/upload/excel \
  -F "file=@/ruta/al/archivo.xlsx" \
  -F "fileName=reporte-ventas.xlsx" \
  -F "destinationPath=/okm:root/documentos/excel" \
  -F "description=Reporte de ventas del mes de octubre"
```

### Ejemplo con PowerShell (Script de prueba)

```powershell
# Usar el script de prueba incluido
.\test-excel-upload.ps1 -FilePath "C:\temp\reporte.xlsx"

# Con parámetros personalizados
.\test-excel-upload.ps1 `
  -FilePath "C:\temp\datos.xlsx" `
  -DestinationPath "/okm:root/reportes" `
  -Description "Datos financieros Q4 2025"
```

### Ejemplo con Java/RestAssured

```java
given()
    .multiPart("file", new File("reporte.xlsx"), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    .multiPart("fileName", "reporte-ventas.xlsx")
    .multiPart("destinationPath", "/okm:root/documentos/excel")
    .multiPart("description", "Reporte de ventas")
.when()
    .post("/api/images/upload/excel")
.then()
    .statusCode(201)
    .body("success", equalTo(true))
    .body("documentId", notNullValue())
    .body("path", containsString("/okm:root/documentos/excel/reporte-ventas.xlsx"));
```

## Validaciones Implementadas

### Validaciones del DTO (Bean Validation)
- `fileName`: No puede estar en blanco (@NotBlank)
- `destinationPath`: No puede estar en blanco (@NotBlank)
- `documentData`: No puede ser nulo (@NotNull)

### Validaciones del Servicio
1. **Datos vacíos**: Verifica que documentData no sea nulo o vacío
2. **Tamaño máximo**: Límite de 50MB (configurable en constante MAX_FILE_SIZE)
3. **Tipo MIME**: Solo acepta formatos Excel válidos (.xlsx y .xls)

### Validaciones del Controlador
1. **Archivo nulo**: Verifica que el FileUpload no sea nulo
2. **Lectura de archivo**: Maneja IOException al leer bytes del archivo
3. **Excepciones generales**: Captura y maneja cualquier excepción inesperada

## Manejo de Errores

### Errores 400 (Bad Request)
- Archivo vacío o nulo
- Tamaño de archivo excede 50MB
- Tipo MIME no válido (no es .xlsx ni .xls)
- Error al leer el archivo del sistema

### Errores 500 (Internal Server Error)
- Error de comunicación con OpenKM
- Error al parsear respuesta de OpenKM
- Excepciones inesperadas durante el procesamiento

### Respuesta de Error (ErrorResponse)
```json
{
  "timestamp": "2025-10-24T17:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "El tamaño del documento excede el límite permitido de 50 MB",
  "path": "/api/images/upload/excel"
}
```

## Documentación OpenAPI/Swagger

La funcionalidad está completamente documentada con anotaciones OpenAPI:
- **@Operation**: Descripción del endpoint
- **@APIResponses**: Códigos de respuesta posibles con ejemplos
- **@Parameter**: Descripción de cada parámetro
- **@Schema**: Esquemas de DTOs con ejemplos

Acceder a la documentación interactiva en:
```
http://localhost:8080/q/swagger-ui
```

## Logging

El sistema incluye logging detallado en múltiples niveles:

### Nivel INFO
- Inicio de subida de documento con ruta y tamaño
- Documento subido exitosamente con UUID y path
- Metadatos del documento (autor, MIME type, etc.)

### Nivel ERROR
- Errores al leer archivos
- Errores de comunicación con OpenKM
- Errores de validación

### Nivel DEBUG
- Parseado de respuestas XML
- Detalles de peticiones HTTP

## Extensibilidad

Esta implementación sirve como plantilla para agregar soporte a otros tipos de documentos:

### Para agregar soporte a PDFs, Word, etc.
1. Crear nuevo DTO (ej: `PdfUploadRequest.java`)
2. Añadir método en `ImageUploadService` (ej: `uploadPdfDocument()`)
3. Añadir validaciones específicas (MIME types, tamaños)
4. Crear endpoint en controlador
5. Documentar con OpenAPI

### Tipos MIME comunes
- **PDF**: `application/pdf`
- **Word**: `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- **PowerPoint**: `application/vnd.openxmlformats-officedocument.presentationml.presentation`

## Pruebas

### Script de Prueba PowerShell
Se incluye `test-excel-upload.ps1` para pruebas rápidas:

**Características:**
- Validación de archivo Excel (.xlsx o .xls)
- Detección automática de MIME type
- Construcción de multipart/form-data
- Manejo de errores con mensajes claros
- Formato de salida colorizado

**Uso:**
```powershell
.\test-excel-upload.ps1 -FilePath "C:\temp\datos.xlsx"
```

## Mejoras Futuras

1. **Validación de contenido**: Verificar que el archivo realmente sea un Excel válido (no solo por extensión)
2. **Metadatos adicionales**: Extraer número de hojas, celdas, etc.
3. **Conversión automática**: Soporte para convertir CSV a Excel
4. **Versionado**: Gestión de versiones de documentos
5. **Búsqueda**: Indexación de contenido de celdas para búsqueda
6. **Preview**: Generación de vista previa o miniatura
7. **Límites configurables**: Tamaño máximo configurable por application.properties
8. **Bulk upload**: Subida de múltiples archivos simultáneamente

## Conclusión

La funcionalidad de subida de documentos Excel ha sido implementada siguiendo los principios de:
- **Reutilización**: Aprovecha componentes existentes (cliente HTTP, DTOs de respuesta, manejadores de excepciones)
- **Consistencia**: Sigue la misma arquitectura que la funcionalidad de imágenes
- **Robustez**: Validaciones exhaustivas y manejo de errores completo
- **Documentación**: OpenAPI completo para fácil consumo de la API
- **Extensibilidad**: Fácil de extender para otros tipos de documentos

La implementación está lista para ser utilizada en producción y puede servir como base para agregar soporte a otros formatos de documentos.
