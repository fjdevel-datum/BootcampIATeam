# Script de prueba para subir documentos Excel a OpenKM usando JSON (Base64)
# Este script prueba el endpoint POST /api/images/upload/excel/json

param(
    [string]$FilePath = "",
    [string]$BaseUrl = "http://localhost:8080",
    [string]$DestinationPath = "/okm:root/documentos/excel",
    [string]$Description = "Documento Excel de prueba (JSON)"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test de Subida de Excel (JSON)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que se proporcionó un archivo
if ([string]::IsNullOrEmpty($FilePath)) {
    Write-Host "Error: Debe proporcionar la ruta del archivo Excel" -ForegroundColor Red
    Write-Host ""
    Write-Host "Uso: .\test-excel-upload-json.ps1 -FilePath 'ruta\al\archivo.xlsx'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Ejemplo:" -ForegroundColor Green
    Write-Host "  .\test-excel-upload-json.ps1 -FilePath 'C:\temp\reporte.xlsx'" -ForegroundColor Green
    Write-Host "  .\test-excel-upload-json.ps1 -FilePath 'C:\temp\datos.xls' -DestinationPath '/okm:root/reportes'" -ForegroundColor Green
    exit 1
}

# Verificar que el archivo existe
if (-not (Test-Path $FilePath)) {
    Write-Host "Error: El archivo no existe: $FilePath" -ForegroundColor Red
    exit 1
}

# Obtener información del archivo
$FileInfo = Get-Item $FilePath
$FileName = $FileInfo.Name
$FileExtension = $FileInfo.Extension.ToLower()

# Validar que sea un archivo Excel
if ($FileExtension -ne ".xlsx" -and $FileExtension -ne ".xls") {
    Write-Host "Error: El archivo debe ser un documento Excel (.xlsx o .xls)" -ForegroundColor Red
    Write-Host "Archivo proporcionado: $FileName ($FileExtension)" -ForegroundColor Yellow
    exit 1
}

# Determinar el MIME type basado en la extensión
$MimeType = if ($FileExtension -eq ".xlsx") {
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
} else {
    "application/vnd.ms-excel"
}

Write-Host "Archivo a subir:" -ForegroundColor Yellow
Write-Host "  Nombre: $FileName" -ForegroundColor White
Write-Host "  Ruta: $FilePath" -ForegroundColor White
Write-Host "  Tamaño: $([math]::Round($FileInfo.Length / 1KB, 2)) KB" -ForegroundColor White
Write-Host "  MIME Type: $MimeType" -ForegroundColor White
Write-Host ""

Write-Host "Destino en OpenKM:" -ForegroundColor Yellow
Write-Host "  Ruta: $DestinationPath" -ForegroundColor White
Write-Host "  Descripción: $Description" -ForegroundColor White
Write-Host ""

$Url = "$BaseUrl/api/images/upload/excel/json"
Write-Host "URL del endpoint: $Url" -ForegroundColor Yellow
Write-Host ""

# Leer el archivo y convertir a Base64
Write-Host "Convirtiendo archivo a Base64..." -ForegroundColor Yellow
$FileBytes = [System.IO.File]::ReadAllBytes($FilePath)
$Base64Content = [System.Convert]::ToBase64String($FileBytes)

Write-Host "  Tamaño del contenido Base64: $($Base64Content.Length) caracteres" -ForegroundColor White
Write-Host ""

# Crear el objeto JSON
$RequestBody = @{
    fileName = $FileName
    destinationPath = $DestinationPath
    documentData = $Base64Content
    description = $Description
    mimeType = $MimeType
} | ConvertTo-Json

Write-Host "Enviando petición JSON..." -ForegroundColor Yellow

try {
    $Response = Invoke-RestMethod -Uri $Url `
        -Method POST `
        -ContentType "application/json" `
        -Body $RequestBody

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ Subida Exitosa" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Respuesta del servidor:" -ForegroundColor Yellow
    $Response | ConvertTo-Json -Depth 10 | Write-Host -ForegroundColor White
    
    Write-Host ""
    Write-Host "Detalles del documento:" -ForegroundColor Yellow
    Write-Host "  Document ID: $($Response.documentId)" -ForegroundColor White
    Write-Host "  Ruta: $($Response.path)" -ForegroundColor White
    Write-Host "  Tamaño: $($Response.size) bytes" -ForegroundColor White
    Write-Host "  Fecha de subida: $($Response.uploadDate)" -ForegroundColor White
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  ✗ Error en la Subida" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    
    if ($_.Exception.Response) {
        $StatusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "Código de error HTTP: $StatusCode" -ForegroundColor Red
        
        $Reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $ResponseBody = $Reader.ReadToEnd()
        $Reader.Close()
        
        Write-Host ""
        Write-Host "Respuesta del servidor:" -ForegroundColor Yellow
        try {
            $ErrorJson = $ResponseBody | ConvertFrom-Json
            $ErrorJson | ConvertTo-Json -Depth 10 | Write-Host -ForegroundColor Red
        } catch {
            Write-Host $ResponseBody -ForegroundColor Red
        }
    } else {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    exit 1
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test completado exitosamente" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
