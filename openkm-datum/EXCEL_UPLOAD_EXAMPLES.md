# Ejemplos de Uso - Subida de Excel a OpenKM

Este documento contiene ejemplos prácticos de cómo utilizar el endpoint de subida de documentos Excel.

---

## 📋 Prerrequisitos

1. **Servicio en ejecución**
   ```powershell
   # Iniciar el servicio Quarkus
   .\mvnw.cmd quarkus:dev
   ```

2. **OpenKM configurado**
   - Verificar `application.properties`:
     ```properties
     openkm.url=http://localhost:8080/OpenKM
     openkm.username=okmAdmin
     openkm.password=admin
     ```

3. **Archivo Excel de prueba**
   - Formato: `.xlsx` o `.xls`
   - Tamaño máximo: 50MB

---

## 🚀 Ejemplos de Uso

### 1. PowerShell (Recomendado para Windows)

#### Opción A: Multipart/Form-Data (Recomendado)

**Ejemplo Básico**
```powershell
# Usar el script incluido
.\test-excel-upload.ps1 -FilePath "C:\temp\reporte.xlsx"
```

#### Ejemplo con Todos los Parámetros
```powershell
.\test-excel-upload.ps1 `
  -FilePath "C:\documentos\ventas-2025.xlsx" `
  -BaseUrl "http://localhost:8080" `
  -DestinationPath "/okm:root/reportes/ventas" `
  -Description "Reporte de ventas del año 2025"
```

#### Ejemplo con Archivo XLS (Excel Antiguo)
```powershell
.\test-excel-upload.ps1 `
  -FilePath "C:\legacy\datos-antiguos.xls" `
  -DestinationPath "/okm:root/historico"
```

#### Opción B: JSON/Base64 (Para Integraciones Programáticas)

**Ejemplo Básico**
```powershell
# Usar el script JSON incluido
.\test-excel-upload-json.ps1 -FilePath "C:\temp\reporte.xlsx"
```

**Ejemplo con Todos los Parámetros**
```powershell
.\test-excel-upload-json.ps1 `
  -FilePath "C:\documentos\ventas-2025.xlsx" `
  -BaseUrl "http://localhost:8080" `
  -DestinationPath "/okm:root/reportes/ventas" `
  -Description "Reporte de ventas del año 2025 (JSON)"
```

**Ejemplo Manual (PowerShell)**
```powershell
# Leer archivo y convertir a Base64
$FilePath = "C:\temp\reporte.xlsx"
$FileBytes = [System.IO.File]::ReadAllBytes($FilePath)
$Base64Content = [System.Convert]::ToBase64String($FileBytes)

# Crear cuerpo JSON
$Body = @{
    fileName = "reporte-ventas.xlsx"
    destinationPath = "/okm:root/documentos/excel"
    documentData = $Base64Content
    description = "Reporte de ventas"
    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
} | ConvertTo-Json

# Enviar petición
$Response = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/images/upload/excel/json" `
    -Method POST `
    -ContentType "application/json" `
    -Body $Body

# Mostrar respuesta
$Response | ConvertTo-Json
```

---

### 2. cURL (Multiplataforma)

#### Opción A: Multipart/Form-Data

**Ejemplo Básico**
```bash
curl -X POST http://localhost:8080/api/images/upload/excel \
  -F "file=@reporte.xlsx" \
  -F "fileName=reporte-ventas.xlsx" \
  -F "destinationPath=/okm:root/documentos/excel" \
  -F "description=Reporte mensual de ventas"
```

#### Ejemplo con Ruta Completa (Windows)
```powershell
curl -X POST http://localhost:8080/api/images/upload/excel `
  -F "file=@C:/temp/datos.xlsx" `
  -F "fileName=datos-financieros.xlsx" `
  -F "destinationPath=/okm:root/finanzas" `
  -F "description=Datos financieros Q4 2025"
```

#### Ejemplo Mínimo (Solo Archivo)
```bash
# El sistema usará valores por defecto para destinationPath
curl -X POST http://localhost:8080/api/images/upload/excel \
  -F "file=@reporte.xlsx" \
  -F "fileName=reporte.xlsx"
```

#### Opción B: JSON/Base64

**Ejemplo con Base64**
```bash
# Convertir archivo a Base64 (Linux/Mac)
BASE64_CONTENT=$(base64 -w 0 reporte.xlsx)

# Enviar petición JSON
curl -X POST http://localhost:8080/api/images/upload/excel/json \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "reporte-ventas.xlsx",
    "destinationPath": "/okm:root/documentos/excel",
    "documentData": "'"$BASE64_CONTENT"'",
    "description": "Reporte de ventas",
    "mimeType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  }'
```

**Ejemplo en Windows (PowerShell + cURL)**
```powershell
# Convertir a Base64
$Base64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes("reporte.xlsx"))

# Crear JSON
$Json = @"
{
  "fileName": "reporte-ventas.xlsx",
  "destinationPath": "/okm:root/documentos/excel",
  "documentData": "$Base64",
  "description": "Reporte de ventas",
  "mimeType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
}
"@

# Enviar con cURL
curl -X POST http://localhost:8080/api/images/upload/excel/json `
  -H "Content-Type: application/json" `
  -d $Json
```

---

### 3. Postman

#### Configuración del Request

1. **Método:** POST
2. **URL:** `http://localhost:8080/api/images/upload/excel`
3. **Body:** Seleccionar `form-data`
4. **Añadir campos:**

| Key | Type | Value | Description |
|-----|------|-------|-------------|
| `file` | File | [Seleccionar archivo .xlsx] | Archivo Excel a subir |
| `fileName` | Text | `reporte-ventas.xlsx` | Nombre en OpenKM |
| `destinationPath` | Text | `/okm:root/documentos/excel` | Ruta de destino |
| `description` | Text | `Reporte de ventas` | Descripción (opcional) |

5. **Enviar**

#### Respuesta Esperada (201 Created)
```json
{
  "documentId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "fileName": "reporte-ventas.xlsx",
  "path": "/okm:root/documentos/excel/reporte-ventas.xlsx",
  "size": 245760,
  "mimeType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  "uploadDate": "2025-10-24T17:30:45",
  "message": "Documento Excel subido exitosamente a OpenKM",
  "success": true
}
```

---

### 4. Java (RestAssured)

#### Dependencia Maven
```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>
```

#### Test de Integración
```java
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ExcelUploadTest {

    @Test
    public void testUploadExcel() {
        File excelFile = new File("src/test/resources/test-data.xlsx");
        
        given()
            .multiPart("file", excelFile, 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .multiPart("fileName", "reporte-ventas.xlsx")
            .multiPart("destinationPath", "/okm:root/test")
            .multiPart("description", "Test de subida de Excel")
        .when()
            .post("http://localhost:8080/api/images/upload/excel")
        .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("documentId", notNullValue())
            .body("path", containsString("/okm:root/test/reporte-ventas.xlsx"))
            .body("mimeType", containsString("spreadsheetml.sheet"));
    }
    
    @Test
    public void testUploadExcelXls() {
        File excelFile = new File("src/test/resources/legacy-data.xls");
        
        given()
            .multiPart("file", excelFile, "application/vnd.ms-excel")
            .multiPart("fileName", "datos-antiguos.xls")
            .multiPart("destinationPath", "/okm:root/historico")
        .when()
            .post("http://localhost:8080/api/images/upload/excel")
        .then()
            .statusCode(201)
            .body("success", equalTo(true));
    }
}
```

---

### 5. Python (requests)

#### Instalación
```bash
pip install requests
```

#### Opción A: Multipart/Form-Data

**Script de Ejemplo**
```python
import requests
import json

def upload_excel(file_path, file_name, destination_path, description=""):
    url = "http://localhost:8080/api/images/upload/excel"
    
    # Abrir el archivo Excel
    with open(file_path, 'rb') as f:
        files = {
            'file': (file_name, f, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
        }
        
        data = {
            'fileName': file_name,
            'destinationPath': destination_path,
            'description': description
        }
        
        # Enviar la petición
        response = requests.post(url, files=files, data=data)
        
        # Procesar respuesta
        if response.status_code == 201:
            result = response.json()
            print("✅ Subida exitosa!")
            print(f"Document ID: {result['documentId']}")
            print(f"Path: {result['path']}")
            print(json.dumps(result, indent=2))
            return result
        else:
            print(f"❌ Error {response.status_code}")
            print(response.text)
            return None

# Uso
if __name__ == "__main__":
    upload_excel(
        file_path="C:/temp/reporte.xlsx",
        file_name="reporte-ventas.xlsx",
        destination_path="/okm:root/documentos/excel",
        description="Reporte de ventas Q4 2025"
    )
```

#### Opción B: JSON/Base64

**Script de Ejemplo**
```python
import requests
import json
import base64

def upload_excel_json(file_path, file_name, destination_path, description=""):
    url = "http://localhost:8080/api/images/upload/excel/json"
    
    # Leer archivo y convertir a Base64
    with open(file_path, 'rb') as f:
        file_content = f.read()
        base64_content = base64.b64encode(file_content).decode('utf-8')
    
    # Preparar el cuerpo JSON
    payload = {
        'fileName': file_name,
        'destinationPath': destination_path,
        'documentData': base64_content,
        'description': description,
        'mimeType': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    }
    
    headers = {
        'Content-Type': 'application/json'
    }
    
    # Enviar petición
    response = requests.post(url, json=payload, headers=headers)
    
    # Procesar respuesta
    if response.status_code == 201:
        result = response.json()
        print("✅ Subida exitosa (JSON)!")
        print(f"Document ID: {result['documentId']}")
        print(f"Path: {result['path']}")
        print(json.dumps(result, indent=2))
        return result
    else:
        print(f"❌ Error {response.status_code}")
        print(response.text)
        return None

# Uso
if __name__ == "__main__":
    upload_excel_json(
        file_path="C:/temp/reporte.xlsx",
        file_name="reporte-ventas.xlsx",
        destination_path="/okm:root/documentos/excel",
        description="Reporte de ventas Q4 2025 (JSON)"
    )
```

---

### 6. JavaScript/Node.js (axios)

#### Instalación
```bash
npm install axios form-data
```

#### Opción A: Multipart/Form-Data

**Script de Ejemplo**
```javascript
const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');

async function uploadExcel(filePath, fileName, destinationPath, description = '') {
    const url = 'http://localhost:8080/api/images/upload/excel';
    
    // Crear form data
    const formData = new FormData();
    formData.append('file', fs.createReadStream(filePath));
    formData.append('fileName', fileName);
    formData.append('destinationPath', destinationPath);
    formData.append('description', description);
    
    try {
        const response = await axios.post(url, formData, {
            headers: formData.getHeaders()
        });
        
        console.log('✅ Subida exitosa!');
        console.log('Document ID:', response.data.documentId);
        console.log('Path:', response.data.path);
        console.log('Response:', JSON.stringify(response.data, null, 2));
        
        return response.data;
    } catch (error) {
        console.error('❌ Error:', error.response?.status);
        console.error('Message:', error.response?.data);
        throw error;
    }
}

// Uso
uploadExcel(
    'C:/temp/reporte.xlsx',
    'reporte-ventas.xlsx',
    '/okm:root/documentos/excel',
    'Reporte de ventas Q4 2025'
).then(result => {
    console.log('Proceso completado');
}).catch(err => {
    console.error('Error en la subida');
});
```

#### Opción B: JSON/Base64

**Script de Ejemplo**
```javascript
const axios = require('axios');
const fs = require('fs');

async function uploadExcelJson(filePath, fileName, destinationPath, description = '') {
    const url = 'http://localhost:8080/api/images/upload/excel/json';
    
    // Leer archivo y convertir a Base64
    const fileBuffer = fs.readFileSync(filePath);
    const base64Content = fileBuffer.toString('base64');
    
    // Preparar el payload JSON
    const payload = {
        fileName: fileName,
        destinationPath: destinationPath,
        documentData: base64Content,
        description: description,
        mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    };
    
    try {
        const response = await axios.post(url, payload, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        console.log('✅ Subida exitosa (JSON)!');
        console.log('Document ID:', response.data.documentId);
        console.log('Path:', response.data.path);
        console.log('Response:', JSON.stringify(response.data, null, 2));
        
        return response.data;
    } catch (error) {
        console.error('❌ Error:', error.response?.status);
        console.error('Message:', error.response?.data);
        throw error;
    }
}

// Uso
uploadExcelJson(
    'C:/temp/reporte.xlsx',
    'reporte-ventas.xlsx',
    '/okm:root/documentos/excel',
    'Reporte de ventas Q4 2025 (JSON)'
).then(result => {
    console.log('Proceso completado');
}).catch(err => {
    console.error('Error en la subida');
});
```

---

### 7. C# (.NET)

#### Código de Ejemplo
```csharp
using System;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

public class ExcelUploadClient
{
    private static readonly HttpClient client = new HttpClient();
    
    public static async Task<string> UploadExcel(
        string filePath, 
        string fileName, 
        string destinationPath,
        string description = "")
    {
        var url = "http://localhost:8080/api/images/upload/excel";
        
        using (var formData = new MultipartFormDataContent())
        {
            // Agregar archivo
            var fileContent = new ByteArrayContent(File.ReadAllBytes(filePath));
            fileContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            formData.Add(fileContent, "file", fileName);
            
            // Agregar parámetros
            formData.Add(new StringContent(fileName), "fileName");
            formData.Add(new StringContent(destinationPath), "destinationPath");
            formData.Add(new StringContent(description), "description");
            
            // Enviar petición
            var response = await client.PostAsync(url, formData);
            var responseBody = await response.Content.ReadAsStringAsync();
            
            if (response.IsSuccessStatusCode)
            {
                Console.WriteLine("✅ Subida exitosa!");
                Console.WriteLine(responseBody);
                return responseBody;
            }
            else
            {
                Console.WriteLine($"❌ Error {response.StatusCode}");
                Console.WriteLine(responseBody);
                throw new Exception($"Error al subir: {response.StatusCode}");
            }
        }
    }
    
    // Uso
    public static async Task Main(string[] args)
    {
        await UploadExcel(
            @"C:\temp\reporte.xlsx",
            "reporte-ventas.xlsx",
            "/okm:root/documentos/excel",
            "Reporte de ventas Q4 2025"
        );
    }
}
```

---

## 🔍 Casos de Uso Específicos

### Caso 1: Subir Reporte Mensual
```powershell
.\test-excel-upload.ps1 `
  -FilePath "C:\reportes\ventas-octubre-2025.xlsx" `
  -DestinationPath "/okm:root/reportes/ventas/2025" `
  -Description "Reporte de ventas del mes de octubre 2025"
```

### Caso 2: Subir Archivo Legacy (.xls)
```bash
curl -X POST http://localhost:8080/api/images/upload/excel \
  -F "file=@datos-antiguos.xls" \
  -F "fileName=datos-2010.xls" \
  -F "destinationPath=/okm:root/historico/2010" \
  -F "description=Datos del sistema legacy migrados"
```

### Caso 3: Organización por Departamentos
```powershell
# Finanzas
.\test-excel-upload.ps1 `
  -FilePath "balance.xlsx" `
  -DestinationPath "/okm:root/departamentos/finanzas"

# Recursos Humanos
.\test-excel-upload.ps1 `
  -FilePath "nomina.xlsx" `
  -DestinationPath "/okm:root/departamentos/rrhh"

# Ventas
.\test-excel-upload.ps1 `
  -FilePath "comisiones.xlsx" `
  -DestinationPath "/okm:root/departamentos/ventas"
```

---

## 📊 Respuestas del API

### Respuesta Exitosa (201 Created)
```json
{
  "documentId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "fileName": "reporte-ventas.xlsx",
  "path": "/okm:root/documentos/excel/reporte-ventas.xlsx",
  "size": 245760,
  "mimeType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  "uploadDate": "2025-10-24T17:30:45",
  "message": "Documento Excel subido exitosamente a OpenKM",
  "success": true
}
```

### Error: Archivo Muy Grande (400 Bad Request)
```json
{
  "timestamp": "2025-10-24T17:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "El tamaño del documento excede el límite permitido de 50 MB",
  "path": "/api/images/upload/excel"
}
```

### Error: Tipo de Archivo Inválido (400 Bad Request)
```json
{
  "timestamp": "2025-10-24T17:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Tipo de documento no válido. Formatos permitidos: .xlsx, .xls",
  "path": "/api/images/upload/excel"
}
```

### Error: OpenKM No Disponible (500 Internal Server Error)
```json
{
  "timestamp": "2025-10-24T17:30:45",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error de conexión con OpenKM: Connection refused",
  "path": "/api/images/upload/excel"
}
```

---

## 🧪 Pruebas con Swagger UI

1. Abrir navegador en: `http://localhost:8080/q/swagger-ui`
2. Expandir sección "Image Upload"
3. Buscar endpoint `POST /api/images/upload/excel`
4. Click en "Try it out"
5. Seleccionar archivo Excel
6. Llenar parámetros requeridos
7. Click en "Execute"
8. Ver respuesta en la sección de resultados

---

## 💡 Consejos y Mejores Prácticas

### 1. Nombres de Archivo
- ✅ Usar nombres descriptivos: `ventas-octubre-2025.xlsx`
- ❌ Evitar caracteres especiales: `ventas@#$%.xlsx`
- ✅ Incluir fechas cuando sea relevante

### 2. Organización de Rutas
```
/okm:root/
├── reportes/
│   ├── ventas/
│   │   └── 2025/
│   ├── finanzas/
│   └── rrhh/
├── departamentos/
│   ├── finanzas/
│   ├── ventas/
│   └── rrhh/
└── historico/
    └── 2024/
```

### 3. Descripciones Útiles
- ✅ "Reporte de ventas Q4 2025 - Región Norte"
- ❌ "documento"
- ✅ "Balance financiero anual - Auditado"

### 4. Validación de Tamaño
```powershell
# PowerShell: Verificar tamaño antes de subir
$file = Get-Item "reporte.xlsx"
$sizeMB = [math]::Round($file.Length / 1MB, 2)
if ($sizeMB -gt 50) {
    Write-Host "Error: Archivo muy grande ($sizeMB MB)" -ForegroundColor Red
} else {
    Write-Host "OK: Tamaño válido ($sizeMB MB)" -ForegroundColor Green
}
```

---

## 🔧 Troubleshooting

### Problema: "Connection refused"
**Solución:** Verificar que OpenKM esté en ejecución
```bash
# Verificar OpenKM
curl http://localhost:8080/OpenKM/

# Verificar servicio
curl http://localhost:8080/api/images/health
```

### Problema: "Archivo muy grande"
**Solución:** Comprimir o dividir el archivo
- Tamaño máximo: 50MB
- Considerar reducir el tamaño del Excel
- Dividir en múltiples archivos si es necesario

### Problema: "Tipo MIME inválido"
**Solución:** Verificar la extensión del archivo
- Permitidos: `.xlsx`, `.xls`
- Verificar que el archivo no esté corrupto

---

## 📚 Recursos Adicionales

- **Documentación completa:** `EXCEL_UPLOAD_DOCUMENTATION.md`
- **Resumen de implementación:** `EXCEL_IMPLEMENTATION_SUMMARY.md`
- **Swagger UI:** `http://localhost:8080/q/swagger-ui`
- **OpenKM API:** `http://localhost:8080/OpenKM/`

---

## ✅ Checklist de Pruebas

- [ ] Subir archivo .xlsx
- [ ] Subir archivo .xls
- [ ] Probar con archivo pequeño (< 1MB)
- [ ] Probar con archivo grande (~ 40MB)
- [ ] Probar con ruta personalizada
- [ ] Probar con descripción
- [ ] Probar sin descripción
- [ ] Verificar documento en OpenKM
- [ ] Probar manejo de errores (archivo muy grande)
- [ ] Probar manejo de errores (tipo inválido)

---

¡Happy coding! 🚀
