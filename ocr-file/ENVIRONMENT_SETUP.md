# 🛠️ Configuración de Variables de Entorno

## 📋 Configuración Inicial

Este proyecto utiliza variables de entorno para manejar configuraciones sensibles como credenciales de base de datos, claves de API y otros parámetros de configuración.

### 1. Copia el archivo de ejemplo

```bash
cp .env.example .env
```

### 2. Configura tus variables de entorno

Edita el archivo `.env` con tus credenciales reales:

## 🔧 Variables de Configuración

### Base de Datos Oracle
- `DB_USERNAME`: Usuario de la base de datos Oracle
- `DB_PASSWORD`: Contraseña de la base de datos Oracle  
- `DB_URL`: URL de conexión a Oracle

### Azure Document Intelligence
- `AZURE_ENDPOINT`: Endpoint del recurso Azure
- `AZURE_API_KEY`: Clave de API de Azure Document Intelligence
- `AZURE_MODEL`: Modelo a utilizar (por defecto: prebuilt-read)

### Hugging Face
- `HUGGINGFACE_TOKEN`: Token de acceso a Hugging Face
- `HUGGINGFACE_API_URL`: URL de la API
- `HUGGINGFACE_MODEL`: Modelo a utilizar

### Configuración de CORS
- `CORS_ORIGINS`: Orígenes permitidos para CORS

### Logging
- `LOG_SQL`: Habilitar logging de SQL (true/false)

## 🔒 Seguridad

- ⚠️ **NUNCA** commitees el archivo `.env` al control de versiones
- ✅ El archivo `.env` ya está incluido en `.gitignore`
- ✅ Usa `.env.example` como plantilla para otros desarrolladores
- 🔄 Rota tus claves periódicamente

## 🚀 Ejecución

Una vez configurado el archivo `.env`, puedes ejecutar la aplicación:

```bash
./mvnw quarkus:dev
```

Quarkus automáticamente cargará las variables del archivo `.env` al inicio de la aplicación.

## 🐳 Docker

Si usas Docker, puedes pasar las variables de entorno al contenedor:

```bash
docker run --env-file .env tu-imagen:tag
```

## 📝 Notas Importantes

1. Las variables de entorno tienen prioridad sobre los valores en `application.properties`
2. Si una variable requerida no está definida, la aplicación fallará al iniciar
3. Algunos valores tienen defaults seguros definidos en `application.properties`
4. Para producción, considera usar servicios de gestión de secretos como:
   - Azure Key Vault
   - AWS Secrets Manager
   - HashiCorp Vault