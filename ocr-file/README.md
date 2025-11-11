# OCR File Processing Application

Aplicación Quarkus para procesamiento de documentos con OCR usando Azure Document Intelligence y análisis de facturas con Hugging Face LLM.

## 🚀 Características

- 📄 **OCR de documentos** usando Azure Document Intelligence
- 🤖 **Análisis inteligente** de facturas con Hugging Face Llama
- 🏢 **Gestión de empresas y países** con API REST completa
- 🗃️ **Base de datos Oracle** con JPA/Hibernate
- 🔒 **Configuración segura** con variables de entorno
- ⚡ **Framework Quarkus** para alto rendimiento

## ⚙️ Configuración Inicial

### 1. Variables de Entorno

Antes de ejecutar la aplicación, configura las variables de entorno:

```bash
cp .env.example .env
```

Edita el archivo `.env` con tus credenciales reales. Ver [ENVIRONMENT_SETUP.md](ENVIRONMENT_SETUP.md) para más detalles.

### 2. Base de Datos

Asegúrate de tener Oracle Database corriendo y configurado según las variables en `.env`.

## 🏃‍♂️ Ejecutar la Aplicación

### Modo Desarrollo

```bash
./mvnw quarkus:dev
```

> **_NOTA:_** Quarkus incluye una Dev UI disponible en: <http://localhost:8080/q/dev/>

## 🛠️ API Endpoints

### Países (Countries)
- `GET /api/countries` - Listar todos los países
- `GET /api/countries/{id}` - Obtener país por ID
- `GET /api/countries/iso/{isoCode}` - Obtener país por código ISO
- `GET /api/countries/search?name=` - Buscar países por nombre
- `POST /api/countries` - Crear nuevo país
- `PUT /api/countries/{id}` - Actualizar país
- `DELETE /api/countries/{id}` - Eliminar país

### Empresas (Companies)
- `GET /api/companies` - Listar todas las empresas
- `GET /api/companies/{id}` - Obtener empresa por ID
- `GET /api/companies/by-country/{countryId}` - Obtener empresas por país
- `GET /api/companies/search?name=` - Buscar empresas por nombre
- `POST /api/companies` - Crear nueva empresa
- `PUT /api/companies/{id}` - Actualizar empresa
- `DELETE /api/companies/{id}` - Eliminar empresa

### OCR y Análisis
- Endpoints de OCR y análisis de facturas (documentados en los controladores existentes)

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/ocr-file-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
