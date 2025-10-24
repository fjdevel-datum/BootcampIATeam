# 🔧 Solución de Problemas - Error 404 al Subir Imágenes

## ❌ Error Identificado

```
status code 404 when invoking REST Client method: OpenKMRestClient#uploadDocument
```

Este error indica que el endpoint de OpenKM no se encuentra en la ruta especificada.

## 🔍 Verificar OpenKM

### 1. Verificar que OpenKM esté corriendo

```powershell
# Verificar si OpenKM responde
Invoke-WebRequest -Uri "http://localhost:8200" -UseBasicParsing

# O en el navegador
Start-Process "http://localhost:8200"
```

### 2. Identificar la versión de OpenKM

OpenKM tiene diferentes versiones con diferentes endpoints REST:

#### OpenKM Community Edition (Versión antigua)
```
http://localhost:8200/OpenKM/services/rest/document/createSimple
```

#### OpenKM Professional/Cloud (Versión nueva)
```
http://localhost:8200/api/document/createSimple
```

### 3. Probar el endpoint manualmente

```powershell
# Crear archivo de prueba
"test" | Out-File -FilePath "test.txt"

# Probar endpoint (ajusta la URL según tu versión)
$uri = "http://localhost:8200/OpenKM/services/rest/document/createSimple"
$auth = "admin:admin"
$bytes = [System.Text.Encoding]::UTF8.GetBytes($auth)
$base64 = [System.Convert]::ToBase64String($bytes)

$headers = @{
    "Authorization" = "Basic $base64"
}

$form = @{
    docPath = "/okm:root/test.txt"
    file = Get-Item "test.txt"
}

try {
    Invoke-RestMethod -Uri $uri -Method Post -Headers $headers -Form $form
    Write-Host "✓ Endpoint funciona!" -ForegroundColor Green
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}
```

## 🛠️ Soluciones

### Solución 1: Actualizar la ruta en el código

Ya hemos actualizado el código para usar:
```java
@Path("/OpenKM/services/rest")
```

Si tu OpenKM usa una ruta diferente, edita:
`src/main/java/org/datum/openkm/client/OpenKMRestClient.java`

```java
@Path("/tu-ruta-correcta")  // Cambia esto
@RegisterRestClient(configKey = "openkm-api")
public interface OpenKMRestClient {
    // ...
}
```

### Solución 2: Verificar configuración en application.properties

Asegúrate de que la URL base sea correcta:

```properties
openkm.api.url=http://localhost:8200
quarkus.rest-client.openkm-api.url=${openkm.api.url}
```

### Solución 3: Endpoints alternativos de OpenKM

Si los anteriores no funcionan, prueba estos:

#### Opción A: REST API estándar
```java
@Path("/OpenKM/services/rest")
```

#### Opción B: API moderna
```java
@Path("/api")
```

#### Opción C: API SOAP (si solo tiene SOAP)
Necesitarás usar un cliente SOAP en lugar de REST

### Solución 4: Usar la API Web Services de OpenKM

Si OpenKM solo tiene SOAP, cambia a:

```xml
<!-- Agregar en pom.xml -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-cxf</artifactId>
</dependency>
```

## 📝 Verificar Documentación de OpenKM

Consulta la documentación oficial según tu versión:

### OpenKM Community Edition
```
http://localhost:8200/OpenKM/help/en/index.html
```

### OpenKM REST API Documentation
```
http://localhost:8200/OpenKM/frontend/index.jsp
```

## 🧪 Script de Diagnóstico

Ejecuta este script para diagnosticar el problema:

```powershell
# Script de diagnóstico de OpenKM
Write-Host "=== Diagnóstico de OpenKM ===" -ForegroundColor Cyan

# 1. Verificar conectividad
Write-Host "`n1. Verificando conectividad..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8200" -UseBasicParsing -TimeoutSec 5
    Write-Host "✓ OpenKM responde (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ No se puede conectar a OpenKM" -ForegroundColor Red
    Write-Host "  Asegúrate de que OpenKM esté corriendo en http://localhost:8200" -ForegroundColor Yellow
    exit
}

# 2. Probar endpoints REST
Write-Host "`n2. Probando endpoints REST..." -ForegroundColor Yellow

$endpoints = @(
    "/OpenKM/services/rest/document/createSimple",
    "/api/document/createSimple",
    "/OpenKM/rest/document/createSimple"
)

$auth = "admin:admin"
$bytes = [System.Text.Encoding]::UTF8.GetBytes($auth)
$base64 = [System.Convert]::ToBase64String($bytes)
$headers = @{ "Authorization" = "Basic $base64" }

foreach ($endpoint in $endpoints) {
    $fullUrl = "http://localhost:8200$endpoint"
    Write-Host "  Probando: $endpoint" -NoNewline
    
    try {
        $response = Invoke-WebRequest -Uri $fullUrl -Method Options -Headers $headers -TimeoutSec 3 -ErrorAction Stop
        Write-Host " ✓ (Status: $($response.StatusCode))" -ForegroundColor Green
    } catch {
        if ($_.Exception.Response.StatusCode -eq 405) {
            Write-Host " ✓ (Existe pero no soporta OPTIONS)" -ForegroundColor Yellow
        } elseif ($_.Exception.Response.StatusCode -eq 404) {
            Write-Host " ✗ (404 Not Found)" -ForegroundColor Red
        } else {
            Write-Host " ? (Status: $($_.Exception.Response.StatusCode))" -ForegroundColor Gray
        }
    }
}

Write-Host "`n3. Recomendaciones:" -ForegroundColor Yellow
Write-Host "  - Consulta la documentación de tu versión de OpenKM"
Write-Host "  - Verifica los logs de OpenKM"
Write-Host "  - Prueba con herramientas como Postman o curl"
```

## 📞 Próximos Pasos

1. **Ejecuta el script de diagnóstico** para identificar el endpoint correcto
2. **Actualiza el código** con el endpoint que funcione
3. **Reinicia la aplicación** Quarkus
4. **Prueba nuevamente** la subida de imágenes

## 🔧 Cambios Realizados

Hemos actualizado:
- ✅ `OpenKMRestClient.java` - Endpoint cambiado a `/OpenKM/services/rest`
- ✅ `ImageUploadService.java` - Uso de File temporal y autenticación explícita

## ⚠️ Nota Importante

Si estás usando una versión específica de OpenKM o una instalación personalizada, puede que necesites ajustar:
- La ruta base del endpoint
- El método de autenticación
- Los parámetros del formulario multipart

---

**Consulta la documentación de tu versión de OpenKM para el endpoint correcto.**
