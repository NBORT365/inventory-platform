# stop_services.ps1
# Uso: powershell -ExecutionPolicy Bypass -File .\stop_services.ps1

$ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ROOT

$pidFiles = Get-ChildItem "$ROOT\logs" -Filter *.pid -ErrorAction SilentlyContinue

if (-not $pidFiles) {
    Write-Host "No se encontraron archivos .pid en $ROOT\logs" -ForegroundColor Yellow
    exit 0
}

foreach ($file in $pidFiles) {
    $pid = Get-Content $file.FullName
    $name = [System.IO.Path]::GetFileNameWithoutExtension($file.FullName)
    if ($pid) {
        try {
            Write-Host "Matando $name (PID $pid)..." -ForegroundColor Cyan
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Remove-Item $file.FullName -Force
        } catch {
            Write-Host "No se pudo matar PID $pid (quizás ya terminó)" -ForegroundColor Yellow
        }
    }
}

Write-Host "Servicios detenidos ✅" -ForegroundColor Green
