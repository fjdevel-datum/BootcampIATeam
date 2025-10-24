# 📁 Índice de Archivos del Proyecto

## 📂 Estructura Completa

### 🔧 Código Fuente Java

#### `src/main/java/org/datum/openkm/client/`
- **OpenKMSDKClient.java** - Cliente HTTP para comunicación con OpenKM

#### `src/main/java/org/datum/openkm/config/`
- **OpenAPIConfig.java** - Configuración de Swagger/OpenAPI
- **OpenKMConfig.java** - Configuración de conexión a OpenKM

#### `src/main/java/org/datum/openkm/controller/`
- **ImageUploadController.java** - Endpoints REST para subida de imágenes

#### `src/main/java/org/datum/openkm/dto/`
- **ErrorResponse.java** - DTO para respuestas de error
- **ImageUploadRequest.java** - DTO para solicitud de subida
- **ImageUploadResponse.java** - DTO para respuesta exitosa

#### `src/main/java/org/datum/openkm/exception/`
- **GlobalExceptionHandler.java** - Manejador global de excepciones
- **ImageUploadException.java** - Excepción personalizada para subida de imágenes
- **OpenKMException.java** - Excepción para errores de OpenKM

#### `src/main/java/org/datum/openkm/service/`
- **ImageUploadService.java** - Lógica de negocio para subida de imágenes

### 📄 Recursos

#### `src/main/resources/`
- **application.properties** - Configuración de la aplicación Quarkus

### 📚 Documentación

- **README.md** - Documentación principal del proyecto
- **API_DOCUMENTATION.md** - Guía completa de la API
- **SDK_IMPLEMENTATION.md** - Detalles de implementación del cliente HTTP
- **QUICK_START.md** - Guía de inicio rápido
- **TROUBLESHOOTING.md** - Solución de problemas comunes
- **CHANGELOG.md** - Historial de cambios
- **REFACTORING_SUMMARY.md** - Resumen de la refactorización

### ⚙️ Configuración

- **.env** - Variables de entorno (no versionado)
- **.env.example** - Plantilla de variables de entorno
- **.gitignore** - Archivos ignorados por Git
- **.dockerignore** - Archivos ignorados por Docker

### 🛠️ Scripts

- **load-env.ps1** - Script PowerShell para cargar variables de entorno
- **test-api.ps1** - Script PowerShell para probar la API

### 📦 Build

- **pom.xml** - Configuración de Maven
- **mvnw** - Maven Wrapper (Unix)
- **mvnw.cmd** - Maven Wrapper (Windows)

### 🐳 Docker

- **src/main/docker/Dockerfile.jvm** - Dockerfile para JVM
- **src/main/docker/Dockerfile.native** - Dockerfile para compilación nativa
- **src/main/docker/Dockerfile.legacy-jar** - Dockerfile legacy
- **src/main/docker/Dockerfile.native-micro** - Dockerfile micro nativo

---

## 📊 Estadísticas

- **Total archivos Java**: 11
- **Total archivos de configuración**: 4
- **Total archivos de documentación**: 7
- **Total scripts**: 2
- **Líneas de código**: ~1,500

---

## 🎯 Archivos Principales

### Para Desarrollo
1. `pom.xml` - Dependencias y configuración
2. `application.properties` - Configuración de la app
3. `.env` - Variables de entorno
4. `OpenKMSDKClient.java` - Cliente HTTP
5. `ImageUploadService.java` - Lógica principal

### Para Uso
1. `README.md` - Empezar aquí
2. `QUICK_START.md` - Guía rápida
3. `test-api.ps1` - Probar la API
4. `swagger-ui` - Documentación interactiva

### Para Referencia
1. `API_DOCUMENTATION.md` - Documentación completa
2. `SDK_IMPLEMENTATION.md` - Detalles técnicos
3. `TROUBLESHOOTING.md` - Solucionar problemas
4. `CHANGELOG.md` - Cambios del proyecto
