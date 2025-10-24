# OpenKM Datum - API REST para Integración con OpenKM# 🚀 OpenKM Datum - Servicio de Integración con OpenKM



API REST desarrollada con **Quarkus** para facilitar la subida de imágenes y documentos a **OpenKM** (Open Knowledge Management).Aplicación Quarkus para la integración con OpenKM para subida de imágenes y documentos.



## 🚀 Características## 📋 Descripción



- ✅ Subida de imágenes a OpenKM via API RESTEste proyecto proporciona una API REST para subir imágenes a OpenKM de forma simple y eficiente. Utiliza Quarkus como framework para ofrecer un servicio rápido, ligero y con hot-reload en desarrollo.

- ✅ Soporte para múltiples formatos (JPEG, PNG, GIF, BMP, WEBP)

- ✅ Validación de archivos (tipo MIME, tamaño máximo 50MB)## ✨ Características

- ✅ Documentación Swagger/OpenAPI integrada

- ✅ Manejo robusto de errores- ✅ API REST para subida de imágenes a OpenKM

- ✅ Cliente HTTP personalizado con Apache HttpClient 5- ✅ Dos métodos de subida: Multipart Form Data y JSON con Base64

- ✅ Autenticación Basic automática- ✅ Validación automática de archivos (tamaño, tipo MIME)

- ✅ CORS habilitado para desarrollo- ✅ Manejo robusto de excepciones

- ✅ Configuración externalizada con variables de entorno

## 📋 Requisitos- ✅ Logging detallado

- ✅ Arquitectura limpia con capas bien definidas

- Java 21+- ✅ Documentación completa con ejemplos

- Maven 3.8+

- OpenKM 6.x+ corriendo en `http://localhost:8200`## 🚀 Inicio Rápido



## 🛠️ Instalación```powershell

# 1. Configurar credenciales

### 1. Configurar variables de entornoCopy-Item .env.example .env

# Edita .env o usa el script

Edita el archivo `.env`:.\load-env.ps1



```properties# 2. Ejecutar en modo desarrollo

OPENKM_API_URL=http://localhost:8200/OpenKM.\mvnw.cmd quarkus:dev

OPENKM_USERNAME=okmAdmin

OPENKM_PASSWORD=admin# 3. Abrir Swagger UI

QUARKUS_HTTP_PORT=8082Start-Process "http://localhost:8082/swagger-ui"

```

# 4. Probar el servicio

### 2. Compilar el proyectoInvoke-RestMethod -Uri "http://localhost:8082/api/images/health"

```

```bash

./mvnw clean installVer [QUICK_START.md](QUICK_START.md) para instrucciones detalladas.

```

## 📖 Documentación

### 3. Ejecutar en modo desarrollo

- **[QUICK_START.md](QUICK_START.md)** - Guía de inicio rápido

```bash- **[SWAGGER_DOCUMENTATION.md](SWAGGER_DOCUMENTATION.md)** - Documentación con Swagger/OpenAPI

./mvnw quarkus:dev- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Documentación completa de la API

```- **[CURL_EXAMPLES.md](CURL_EXAMPLES.md)** - Ejemplos de uso con cURL y PowerShell

- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Resumen de la implementación

La aplicación estará disponible en: `http://localhost:8082`

## 🎯 Endpoints

## 📝 Uso de la API

| Método | Endpoint | Descripción |

### 1. Subir Imagen (Multipart)|--------|----------|-------------|

| GET | `/swagger-ui` | 📖 Documentación interactiva Swagger UI |

```bash| GET | `/openapi` | 📄 Especificación OpenAPI (JSON/YAML) |

curl -X POST http://localhost:8082/api/images/upload \| GET | `/api/images/health` | Verificar estado del servicio |

  -F "file=@image.jpg" \| POST | `/api/images/upload` | Subir imagen (Multipart Form Data) |

  -F "fileName=test.jpg" \| POST | `/api/images/upload/json` | Subir imagen (JSON con Base64) |

  -F "destinationPath=/okm:root" \

  -F "mimeType=image/jpeg"## 🛠️ Tecnologías

```

- **Java 21**

### 2. Subir Imagen (JSON con Base64)- **Quarkus 3.28.4** - Framework supersónico y subatómico

- **Maven** - Gestión de dependencias

```bash- **MicroProfile REST Client** - Cliente REST declarativo

curl -X POST http://localhost:8082/api/images/upload/json \- **Hibernate Validator** - Validación de datos

  -H "Content-Type: application/json" \- **Jackson** - Serialización JSON

  -d '{

    "fileName": "test.jpg",## ⚙️ Configuración

    "destinationPath": "/okm:root",

    "mimeType": "image/jpeg",### Requisitos Previos

    "imageData": "<base64>"- Java 21+

  }'- Maven 3.8+

```- OpenKM corriendo en `http://localhost:8200`



### 3. Health Check### Variables de Entorno

```bash

```bashOPENKM_USERNAME=admin

curl http://localhost:8082/api/images/healthOPENKM_PASSWORD=admin

``````



## 📚 Documentación### Puertos

- Quarkus: `8082`

- **Swagger UI**: http://localhost:8082/swagger-ui- OpenKM: `8200`

- **OpenAPI**: http://localhost:8082/openapi

- [API Documentation](API_DOCUMENTATION.md)## 🧪 Ejecutar en Modo Desarrollo

- [SDK Implementation](SDK_IMPLEMENTATION.md)

- [Quick Start](QUICK_START.md)```bash

- [Troubleshooting](TROUBLESHOOTING.md)./mvnw quarkus:dev

```

## 🏗️ Arquitectura

> **Nota:** El modo desarrollo incluye hot-reload y la Dev UI en <http://localhost:8082/q/dev/>

```

src/main/java/org/datum/openkm/## 📦 Empaquetar la Aplicación

├── client/          # Cliente HTTP para OpenKM

├── config/          # Configuración de la aplicación```bash

├── controller/      # Endpoints REST# JAR estándar

├── dto/             # Objetos de transferencia de datos./mvnw package

├── exception/       # Manejo de excepciones

└── service/         # Lógica de negocio# Ejecutar

```java -jar target/quarkus-app/quarkus-run.jar

```

## ⚙️ Configuración

## 🧰 Probar la API

```properties

# application.properties### Usando el Script de Prueba

quarkus.http.port=8082```powershell

openkm.api.url=http://localhost:8200/OpenKM.\test-api.ps1

openkm.api.username=${OPENKM_USERNAME:okmAdmin}```

openkm.api.password=${OPENKM_PASSWORD:admin}

quarkus.http.limits.max-body-size=50M### Usando PowerShell

``````powershell

$form = @{

## 🔍 Troubleshooting    file = Get-Item -Path "imagen.jpg"

    fileName = "mi-imagen.jpg"

### Puerto 8082 en uso    destinationPath = "/okm:root/images"

    mimeType = "image/jpeg"

```bash}

# Windows

netstat -ano | findstr :8082Invoke-RestMethod -Uri "http://localhost:8082/api/images/upload" -Method Post -Form $form

taskkill /PID <pid> /F```

```

### Usando cURL

### Conexión rechazada con OpenKM```bash

curl -X POST http://localhost:8082/api/images/upload \

1. Verifica que OpenKM esté en `http://localhost:8200`  -F "file=@imagen.jpg" \

2. Revisa credenciales en `.env`  -F "fileName=mi-imagen.jpg" \

3. Prueba: `curl http://localhost:8082/api/images/health`  -F "destinationPath=/okm:root/images"

```

### PathNotFoundException

## 📁 Estructura del Proyecto

El directorio no existe en OpenKM. Usa rutas existentes como `/okm:root`.

```

## 📄 Licenciasrc/main/java/org/datum/openkm/

├── client/         # Clientes REST

MIT License├── config/         # Configuración

├── controller/     # Controladores REST

## 👥 Autores├── dto/            # Data Transfer Objects

├── exception/      # Excepciones y manejadores

Datum Team└── service/        # Lógica de negocio

```

## 🔍 Validaciones

- Tamaño máximo: **50 MB**
- Tipos MIME soportados:
  - `image/jpeg`
  - `image/png`
  - `image/gif`
  - `image/bmp`
  - `image/webp`

## 🐛 Solución de Problemas

### OpenKM no responde
```powershell
# Verificar que OpenKM está corriendo
Invoke-WebRequest -Uri "http://localhost:8200" -UseBasicParsing
```

### Puerto ocupado
Cambiar en `application.properties`:
```properties
quarkus.http.port=8083
```

### Error de compilación
```bash
./mvnw clean install
```

## 📚 Más información sobre Quarkus

Si quieres aprender más sobre Quarkus, visita: <https://quarkus.io/>

### Ejecutable Nativo

Crear un ejecutable nativo:
```bash
./mvnw package -Dnative
```

O con Docker:
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Ejecutar:
```bash
./target/openkm-datum-1.0.0-SNAPSHOT-runner
```

## 📝 Licencia

Este proyecto está bajo la licencia MIT.

## 👥 Contribuir

Las contribuciones son bienvenidas. Por favor, abre un issue o pull request.

---

**Desarrollado con ❤️ usando Quarkus**
