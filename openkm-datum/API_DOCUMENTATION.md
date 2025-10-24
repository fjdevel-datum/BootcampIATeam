# OpenKM Integration Service

Servicio de integración con OpenKM para subida de imágenes utilizando Quarkus.

## Características

- ✅ Subida de imágenes a OpenKM mediante API REST
- ✅ Dos endpoints: Multipart Form Data y JSON
- ✅ Validación de archivos (tamaño, tipo MIME)
- ✅ Manejo de excepciones personalizado
- ✅ Configuración externalizada
- ✅ Logging detallado
- ✅ Arquitectura limpia con DTOs, servicios y controladores

## Requisitos Previos

- Java 21+
- Maven 3.8+
- OpenKM corriendo en `http://localhost:8200`

## Configuración

Las credenciales de OpenKM se pueden configurar mediante variables de entorno:

```bash
export OPENKM_USERNAME=admin
export OPENKM_PASSWORD=admin
```

O editar el archivo `src/main/resources/application.properties`.

## Ejecutar la Aplicación

### Modo Desarrollo

```bash
./mvnw clean quarkus:dev
```

### Modo Producción

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## Endpoints Disponibles

### 1. Health Check
```bash
GET http://localhost:8082/api/images/health
```

### 2. Subida de Imagen (Multipart Form Data)

```bash
curl -X POST http://localhost:8082/api/images/upload \
  -F "file=@/ruta/a/tu/imagen.jpg" \
  -F "fileName=mi-imagen.jpg" \
  -F "destinationPath=/okm:root/images" \
  -F "description=Descripción de la imagen" \
  -F "mimeType=image/jpeg"
```

**Parámetros:**
- `file` (requerido): Archivo de imagen
- `fileName` (requerido): Nombre del archivo en OpenKM
- `destinationPath` (opcional): Ruta de destino en OpenKM (default: `/okm:root/images`)
- `description` (opcional): Descripción del documento
- `mimeType` (opcional): Tipo MIME de la imagen

### 3. Subida de Imagen (JSON)

```bash
curl -X POST http://localhost:8082/api/images/upload/json \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "mi-imagen.jpg",
    "destinationPath": "/okm:root/images",
    "imageData": "base64EncodedImageData...",
    "description": "Descripción de la imagen",
    "mimeType": "image/jpeg"
  }'
```

## Respuesta Exitosa

```json
{
  "documentId": "uuid-del-documento",
  "fileName": "mi-imagen.jpg",
  "path": "/okm:root/images/mi-imagen.jpg",
  "size": 524288,
  "mimeType": "image/jpeg",
  "uploadDate": "2025-10-23T10:30:00",
  "message": "Imagen subida exitosamente",
  "success": true
}
```

## Respuesta de Error

```json
{
  "message": "Error al subir la imagen",
  "statusCode": 500,
  "timestamp": "2025-10-23T10:30:00",
  "path": "/api/images/upload",
  "details": ["Detalle del error"]
}
```

## Validaciones

- **Tamaño máximo**: 50 MB
- **Tipos MIME permitidos**: 
  - `image/jpeg`
  - `image/png`
  - `image/gif`
  - `image/bmp`
  - `image/webp`

## Estructura del Proyecto

```
src/main/java/org/datum/openkm/
├── client/
│   └── OpenKMRestClient.java          # Cliente REST para OpenKM
├── config/
│   └── OpenKMConfig.java              # Configuración de OpenKM
├── controller/
│   └── ImageUploadController.java     # Controlador REST
├── dto/
│   ├── ErrorResponse.java             # DTO para respuestas de error
│   ├── ImageUploadRequest.java        # DTO para solicitudes
│   └── ImageUploadResponse.java       # DTO para respuestas
├── exception/
│   ├── GlobalExceptionHandler.java    # Manejador global de excepciones
│   ├── ImageUploadException.java      # Excepción específica
│   └── OpenKMException.java           # Excepción base
└── service/
    └── ImageUploadService.java        # Servicio de lógica de negocio
```

## Testing

Para probar la API con una imagen de ejemplo:

```bash
# Crear una imagen de prueba
echo "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==" | base64 -d > test.png

# Subir la imagen
curl -X POST http://localhost:8082/api/images/upload \
  -F "file=@test.png" \
  -F "fileName=test.png" \
  -F "destinationPath=/okm:root/test" \
  -F "mimeType=image/png"
```

## Troubleshooting

### OpenKM no está disponible
Asegúrate de que OpenKM esté corriendo en `http://localhost:8200` y que las credenciales sean correctas.

### Error de tamaño de archivo
Verifica que el archivo no exceda los 50 MB. Puedes ajustar este límite en `application.properties`:

```properties
quarkus.http.limits.max-body-size=100M
```

### Error de autenticación
Verifica las credenciales de OpenKM en las variables de entorno o en `application.properties`.

## Licencia

MIT
