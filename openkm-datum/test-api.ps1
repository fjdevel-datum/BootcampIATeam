# Script de prueba para la API de subida de imágenes
# PowerShell

Write-Host "=== Test de la API de Subida de Imágenes a OpenKM ===" -ForegroundColor Cyan

# 1. Health Check
Write-Host "`n1. Verificando el estado del servicio..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/images/health" -Method Get
    Write-Host "✓ Servicio activo: $healthResponse" -ForegroundColor Green
} catch {
    Write-Host "✗ Error al conectar con el servicio: $_" -ForegroundColor Red
    exit 1
}

# 2. Crear una imagen de prueba
Write-Host "`n2. Creando imagen de prueba..." -ForegroundColor Yellow
$testImagePath = "$env:TEMP\test-image.png"

# Imagen PNG de 1x1 pixel (base64)
$base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
$imageBytes = [Convert]::FromBase64String($base64Image)
[IO.File]::WriteAllBytes($testImagePath, $imageBytes)

Write-Host "✓ Imagen creada en: $testImagePath" -ForegroundColor Green

# 3. Subir imagen usando Multipart Form Data
Write-Host "`n3. Subiendo imagen a OpenKM (Multipart)..." -ForegroundColor Yellow

$fileName = "test-image-$(Get-Date -Format 'yyyyMMdd-HHmmss').png"
$destinationPath = "/okm:root/test/images"

try {
    $form = @{
        file = Get-Item -Path $testImagePath
        fileName = $fileName
        destinationPath = $destinationPath
        description = "Imagen de prueba subida desde PowerShell"
        mimeType = "image/png"
    }

    $uploadResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/images/upload" `
        -Method Post `
        -Form $form `
        -ContentType "multipart/form-data"

    Write-Host "✓ Imagen subida exitosamente!" -ForegroundColor Green
    Write-Host "  Document ID: $($uploadResponse.documentId)" -ForegroundColor Cyan
    Write-Host "  File Name: $($uploadResponse.fileName)" -ForegroundColor Cyan
    Write-Host "  Path: $($uploadResponse.path)" -ForegroundColor Cyan
    Write-Host "  Size: $($uploadResponse.size) bytes" -ForegroundColor Cyan
    Write-Host "  Upload Date: $($uploadResponse.uploadDate)" -ForegroundColor Cyan
    Write-Host "  Message: $($uploadResponse.message)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Error al subir la imagen: $_" -ForegroundColor Red
    Write-Host $_.Exception.Response.StatusCode -ForegroundColor Red
    
    # Intentar leer el cuerpo de la respuesta de error
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Detalle del error: $responseBody" -ForegroundColor Red
    }
}

# 4. Limpiar archivo temporal
Write-Host "`n4. Limpiando archivos temporales..." -ForegroundColor Yellow
Remove-Item -Path $testImagePath -Force
Write-Host "✓ Limpieza completada" -ForegroundColor Green

Write-Host "`n=== Prueba completada ===" -ForegroundColor Cyan
