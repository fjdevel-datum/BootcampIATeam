# Implementación con OpenKM SDK

## 📋 Resumen

El proyecto ha sido **refactorizado** para utilizar el **SDK oficial de Java de OpenKM** en lugar del REST API directo. Esto proporciona:

✅ Mayor estabilidad y compatibilidad  
✅ Manejo automático de la comunicación SOAP/REST  
✅ Métodos de alto nivel para operaciones comunes  
✅ Mejor manejo de errores  

---

## 🔧 Cambios Realizados

### 1. **Dependencias Actualizadas** (`pom.xml`)

```xml
<!-- OpenKM SDK -->
<dependency>
    <groupId>com.openkm</groupId>
    <artifactId>openkm-sdk4j</artifactId>
    <version>6.3.12</version>
</dependency>

<!-- HTTP Client para OpenKM SDK -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.14</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpmime</artifactId>
    <version>4.5.14</version>
</dependency>
```

### 2. **Nuevo Cliente SDK** (`OpenKMSDKClient.java`)

Reemplaza al antiguo `OpenKMRestClient.java` y proporciona:

- ✅ Inicialización automática del cliente SDK
- ✅ Métodos para subir documentos
- ✅ Operaciones de gestión de carpetas
- ✅ Verificación de existencia de documentos
- ✅ Test de conectividad

**Métodos principales:**

```java
public Document uploadDocument(String docPath, byte[] content, String mimeType)
public boolean documentExists(String docPath)
public void deleteDocument(String docPath)
public Document getDocumentProperties(String docPath)
public void createFolder(String folderPath)
public boolean testConnection()
```

### 3. **Servicio Actualizado** (`ImageUploadService.java`)

Ahora utiliza `OpenKMSDKClient` en lugar del REST client:

```java
@Inject
OpenKMSDKClient openKMClient;

public ImageUploadResponse uploadImage(ImageUploadRequest request) {
    // ...validaciones...
    
    Document document = openKMClient.uploadDocument(
        fullPath,
        request.getImageData(),
        request.getMimeType()
    );
    
    return ImageUploadResponse.builder()
        .documentId(document.getUuid())
        .path(document.getPath())
        // ...
        .build();
}
```

**Ventajas:**
- ❌ Ya no requiere archivos temporales
- ❌ Ya no requiere manejo manual de autenticación Basic
- ✅ Trabaja directamente con bytes
- ✅ Retorna objetos `Document` completos con metadata

### 4. **Configuración Actualizada** (`application.properties`)

```properties
# Configuración de OpenKM SDK
openkm.api.url=http://localhost:8200/OpenKM
openkm.api.username=${OPENKM_USERNAME:okmAdmin}
openkm.api.password=${OPENKM_PASSWORD:admin}
```

**Cambios importantes:**
- URL ahora incluye `/OpenKM` base path
- Usuario por defecto es `okmAdmin` (usuario por defecto de OpenKM)

### 5. **Variables de Entorno** (`.env`)

```env
OPENKM_API_URL=http://localhost:8200/OpenKM
OPENKM_USERNAME=okmAdmin
OPENKM_PASSWORD=admin
```

---

## 🚀 Instalación y Ejecución

### 1. **Instalar Dependencias**

```powershell
.\mvnw clean install
```

### 2. **Cargar Variables de Entorno**

```powershell
.\load-env.ps1
```

### 3. **Ejecutar la Aplicación**

```powershell
.\mvnw quarkus:dev
```

---

## 📝 Endpoints Disponibles

### 1. **Subir Imagen (Multipart)**

```bash
POST http://localhost:8082/api/images/upload
Content-Type: multipart/form-data

fileName=test.jpg
destinationPath=/okm:root/images
mimeType=image/jpeg
file=<archivo>
```

### 2. **Subir Imagen (JSON)**

```bash
POST http://localhost:8082/api/images/upload/json
Content-Type: application/json

{
  "fileName": "test.jpg",
  "destinationPath": "/okm:root/images",
  "mimeType": "image/jpeg",
  "imageData": "<base64>"
}
```

### 3. **Health Check**

```bash
GET http://localhost:8082/api/images/health
```

**Respuesta:**
```json
{
  "status": "UP",
  "service": "OpenKM Image Upload API",
  "openKM": {
    "connected": true,
    "sdk": "OpenKM SDK4J 6.3.12"
  },
  "timestamp": "2025-10-23T16:30:00"
}
```

---

## 🧪 Probar la API

### Usar PowerShell Script:

```powershell
.\test-api.ps1
```

### Usar cURL:

```powershell
curl -X POST http://localhost:8082/api/images/upload/json `
  -H "Content-Type: application/json" `
  -d '{
    "fileName": "test.jpg",
    "destinationPath": "/okm:root/images",
    "mimeType": "image/jpeg",
    "imageData": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
  }'
```

---

## 🔍 Verificar Conexión con OpenKM

```powershell
curl http://localhost:8082/api/images/health
```

Si OpenKM está corriendo y accesible, deberías ver `"connected": true`.

---

## 📚 Documentación API

Accede a Swagger UI en:

```
http://localhost:8082/swagger-ui
```

---

## 🛠️ Troubleshooting

### Error: "Cannot resolve com.openkm"

**Solución:** Ejecutar Maven con update de dependencias:
```powershell
.\mvnw clean install -U
```

### Error: "Connection refused" al conectar con OpenKM

**Verificar:**
1. OpenKM está corriendo: `http://localhost:8200`
2. URL correcta en `.env`: debe incluir `/OpenKM`
3. Credenciales correctas (usuario: `okmAdmin`, password: `admin`)

### Error: "Unauthorized" al subir archivos

**Verificar:**
1. Usuario y contraseña en `.env` son correctos
2. El usuario tiene permisos de escritura en `/okm:root/images`

---

## 🎯 Ventajas del SDK vs REST API

| Aspecto | REST API | SDK |
|---------|----------|-----|
| **Configuración** | Manual (annotations, params) | Automática |
| **Autenticación** | Manual (Basic Auth headers) | Automática |
| **Manejo de Errores** | HTTP status codes | Excepciones tipadas |
| **Tipos de Datos** | Strings, streams | Objetos Document |
| **Complejidad** | Alta | Baja |
| **Mantenimiento** | Requiere updates manuales | Manejado por SDK |
| **Debugging** | Difícil (406, 500, etc.) | Más claro |

---

## 📖 Referencias

- [OpenKM SDK4J GitHub](https://github.com/openkm/document-management-system)
- [OpenKM Documentation](https://docs.openkm.com/)
- [Quarkus Guide](https://quarkus.io/guides/)

---

## ✅ Checklist de Migración

- [x] Agregar dependencias del SDK
- [x] Crear `OpenKMSDKClient`
- [x] Actualizar `ImageUploadService`
- [x] Actualizar `application.properties`
- [x] Actualizar `.env` y `.env.example`
- [x] Eliminar código REST cliente antiguo
- [x] Agregar test de conectividad
- [x] Actualizar documentación

---

¡La implementación con SDK está lista para usar! 🚀
