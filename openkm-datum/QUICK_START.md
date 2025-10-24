# 🚀 Guía de Inicio Rápido

Esta guía te ayudará a poner en marcha el servicio de integración con OpenKM en minutos.

## 📋 Requisitos Previos

- ✅ Java 21 o superior
- ✅ Maven 3.8+
- ✅ OpenKM corriendo en `http://localhost:8200`

## ⚡ Inicio Rápido (3 pasos)

### 1️⃣ Configurar Variables de Entorno

```powershell
# Opción A: Copiar y editar .env
Copy-Item .env.example .env
# Editar .env con tus credenciales

# Opción B: Cargar .env automáticamente
.\load-env.ps1
```

O configurar manualmente:

```powershell
$env:OPENKM_USERNAME="admin"
$env:OPENKM_PASSWORD="admin"
$env:OPENKM_API_URL="http://localhost:8200"
```

### 2️⃣ Ejecutar la Aplicación

```powershell
# Modo desarrollo (con hot-reload)
.\mvnw.cmd quarkus:dev
```

### 3️⃣ Probar la API

Abre tu navegador en:
- 🔍 **Swagger UI**: http://localhost:8082/swagger-ui
- 📄 **OpenAPI Spec**: http://localhost:8082/openapi
- ❤️ **Health Check**: http://localhost:8082/api/images/health

## 📊 Usando Swagger UI

1. Ve a http://localhost:8082/swagger-ui
2. Expande el endpoint `POST /api/images/upload`
3. Click en "Try it out"
4. Selecciona un archivo de imagen
5. Completa los campos requeridos
6. Click en "Execute"
7. ¡Listo! Verás la respuesta debajo

## 🧪 Probar con Script

```powershell
# Ejecutar script de prueba
.\test-api.ps1
```

## 📝 Ejemplo Rápido con PowerShell

```powershell
# Crear datos del formulario
$form = @{
    file = Get-Item -Path "imagen.jpg"
    fileName = "mi-imagen.jpg"
    destinationPath = "/okm:root/images"
    mimeType = "image/jpeg"
}

# Subir imagen
$response = Invoke-RestMethod `
    -Uri "http://localhost:8082/api/images/upload" `
    -Method Post `
    -Form $form

# Mostrar resultado
Write-Host "✓ Imagen subida: $($response.path)"
```

## 📝 Ejemplo con cURL

```bash
curl -X POST http://localhost:8082/api/images/upload \
  -F "file=@imagen.jpg" \
  -F "fileName=mi-imagen.jpg" \
  -F "destinationPath=/okm:root/images" \
  -F "mimeType=image/jpeg"
```

## 🔧 Configuración Adicional

### Variables de Entorno Importantes

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `QUARKUS_HTTP_PORT` | Puerto del servidor | `8082` |
| `OPENKM_API_URL` | URL de OpenKM | `http://localhost:8200` |
| `OPENKM_USERNAME` | Usuario de OpenKM | `admin` |
| `OPENKM_PASSWORD` | Contraseña de OpenKM | `admin` |

### Cambiar Puerto

```powershell
# En .env o mediante variable de entorno
$env:QUARKUS_HTTP_PORT="8083"
.\mvnw.cmd quarkus:dev
```

### Habilitar CORS para Frontend

Ya está habilitado por defecto para desarrollo. Para producción, edita `application.properties`:

```properties
quarkus.http.cors.origins=https://tudominio.com
```

## 🐛 Solución de Problemas

### OpenKM no responde
```powershell
# Verificar que OpenKM está corriendo
Invoke-WebRequest -Uri "http://localhost:8200" -UseBasicParsing
```

### Puerto 8082 ocupado
```powershell
# Usar otro puerto
$env:QUARKUS_HTTP_PORT="8083"
.\mvnw.cmd quarkus:dev
```

### Error de compilación
```powershell
# Limpiar y recompilar
.\mvnw.cmd clean install
```

### Variables de entorno no cargadas
```powershell
# Cargar variables manualmente
.\load-env.ps1
```

## 📚 Siguientes Pasos

1. 📖 Lee la [documentación completa de la API](API_DOCUMENTATION.md)
2. 💻 Revisa los [ejemplos de cURL](CURL_EXAMPLES.md)
3. 🔍 Explora la API con [Swagger UI](http://localhost:8082/swagger-ui)
4. 🧪 Prueba diferentes escenarios con el [script de prueba](test-api.ps1)

## 🎯 Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/swagger-ui` | Interfaz Swagger UI |
| GET | `/openapi` | Especificación OpenAPI |
| GET | `/api/images/health` | Health check |
| POST | `/api/images/upload` | Subir imagen (Multipart) |
| POST | `/api/images/upload/json` | Subir imagen (JSON) |

## ⚠️ Importante

- **Desarrollo**: La configuración actual es para desarrollo local
- **Producción**: Cambia las credenciales y configura CORS apropiadamente
- **Seguridad**: No commites el archivo `.env` al repositorio

## 📞 Soporte

¿Tienes problemas? Revisa:
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Documentación detallada
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Detalles técnicos

---

**¡Listo para empezar! 🚀**
