# Script de prueba para subir documentos Excel a OpenKM
# Este script prueba el endpoint POST /api/images/upload/excel

param(
    [string]$FilePath = "",
    [string]$BaseUrl = "http://localhost:8080",
    [string]$DestinationPath = "/okm:root/documentos/excel",
    [string]$Description = "Documento Excel de prueba"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test de Subida de Excel a OpenKM" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que se proporcionó un archivo
if ([string]::IsNullOrEmpty($FilePath)) {
    Write-Host "Error: Debe proporcionar la ruta del archivo Excel" -ForegroundColor Red
    Write-Host ""
    Write-Host "Uso: .\test-excel-upload.ps1 -FilePath 'ruta\al\archivo.xlsx'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Ejemplo:" -ForegroundColor Green
    Write-Host "  .\test-excel-upload.ps1 -FilePath 'C:\temp\reporte.xlsx'" -ForegroundColor Green
    Write-Host "  .\test-excel-upload.ps1 -FilePath 'C:\temp\datos.xls' -DestinationPath '/okm:root/reportes'" -ForegroundColor Green
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

$Url = "$BaseUrl/api/images/upload/excel"
Write-Host "URL del endpoint: $Url" -ForegroundColor Yellow
Write-Host ""

# Leer el archivo como bytes
$FileBytes = [System.IO.File]::ReadAllBytes($FilePath)

# Crear el boundary para multipart/form-data
$Boundary = [System.Guid]::NewGuid().ToString()

# Crear el cuerpo de la petición multipart
$LF = "`r`n"
$BodyLines = @(
    "--$Boundary",
    "Content-Disposition: form-data; name=`"file`"; filename=`"$FileName`"",
    "Content-Type: $MimeType",
    "",
    [System.Text.Encoding]::GetEncoding("iso-8859-1").GetString($FileBytes),
    "--$Boundary",
    "Content-Disposition: form-data; name=`"fileName`"",
    "",
    $FileName,
    "--$Boundary",
    "Content-Disposition: form-data; name=`"destinationPath`"",
    "",
    $DestinationPath,
    "--$Boundary",
    "Content-Disposition: form-data; name=`"description`"",
    "",
    $Description,
    "--$Boundary--"
)

$Body = $BodyLines -join $LF

Write-Host "Enviando petición..." -ForegroundColor Yellow

try {
    $Response = Invoke-WebRequest -Uri $Url `
        -Method POST `
        -ContentType "multipart/form-data; boundary=$Boundary" `
        -Body ([System.Text.Encoding]::GetEncoding("iso-8859-1").GetBytes($Body)) `
        -UseBasicParsing

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ Subida Exitosa" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Código de respuesta: $($Response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    Write-Host "Respuesta del servidor:" -ForegroundColor Yellow
    
    # Parsear y mostrar JSON formateado
    $JsonResponse = $Response.Content | ConvertFrom-Json
    $JsonResponse | ConvertTo-Json -Depth 10 | Write-Host -ForegroundColor White
    
    Write-Host ""
    Write-Host "Detalles del documento:" -ForegroundColor Yellow
    Write-Host "  Document ID: $($JsonResponse.documentId)" -ForegroundColor White
    Write-Host "  Ruta: $($JsonResponse.path)" -ForegroundColor White
    Write-Host "  Tamaño: $($JsonResponse.size) bytes" -ForegroundColor White
    Write-Host "  Fecha de subida: $($JsonResponse.uploadDate)" -ForegroundColor White
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
