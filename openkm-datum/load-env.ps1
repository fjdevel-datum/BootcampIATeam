# Script para cargar variables de entorno desde .env
# Uso: .\load-env.ps1

Write-Host "=== Cargando Variables de Entorno ===" -ForegroundColor Cyan

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Host "✗ Archivo .env no encontrado" -ForegroundColor Red
    Write-Host "  Copia .env.example a .env y configura las variables" -ForegroundColor Yellow
    exit 1
}

Write-Host "📄 Leyendo: $envFile" -ForegroundColor Yellow

$loadedCount = 0
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    
    # Ignorar líneas vacías y comentarios
    if ($line -and -not $line.StartsWith("#")) {
        if ($line -match '^([^=]+)=(.*)$') {
            $key = $matches[1].Trim()
            $value = $matches[2].Trim()
            
            # Remover comillas si existen
            $value = $value -replace '^["'']|["'']$', ''
            
            [Environment]::SetEnvironmentVariable($key, $value, "Process")
            Write-Host "  ✓ $key" -ForegroundColor Green
            $loadedCount++
        }
    }
}

Write-Host "`n✅ $loadedCount variables de entorno cargadas" -ForegroundColor Green
Write-Host "`nVariables principales configuradas:" -ForegroundColor Cyan
Write-Host "  - QUARKUS_HTTP_PORT: $env:QUARKUS_HTTP_PORT"
Write-Host "  - OPENKM_API_URL: $env:OPENKM_API_URL"
Write-Host "  - OPENKM_USERNAME: $env:OPENKM_USERNAME"
Write-Host ""
