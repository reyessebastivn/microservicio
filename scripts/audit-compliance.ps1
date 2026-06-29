# ─────────────────────────────────────────────────────────────
#  Script de Auditoría de Cumplimiento Técnico (Windows)
#  Verifica políticas básicas de seguridad y calidad localmente
# ─────────────────────────────────────────────────────────────

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "       INICIO DE AUDITORÍA DE CUMPLIMIENTO (WINDOWS)" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

# Navegar a la raíz del repositorio Git
$gitRoot = git rev-parse --show-toplevel
if ($gitRoot) {
    Set-Location $gitRoot
}

$fail = $false

# 1. Verificar .env en .gitignore
Write-Host "[AUDIT] 1. Verificando que .env esté en .gitignore..."
if (Test-Path .gitignore) {
    $gitignore = Get-Content .gitignore
    if ($gitignore -match "^\.env$") {
        Write-Host "  [OK] .env está correctamente configurado en .gitignore." -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] .env no se encuentra en .gitignore!" -ForegroundColor Red
        $fail = $true
    }
} else {
    Write-Host "  [FAIL] No se encontró el archivo .gitignore!" -ForegroundColor Red
    $fail = $true
}

# 2. Verificar secretos en código
Write-Host "[AUDIT] 2. Buscando posibles credenciales harcodeadas..."
$pattern = 'password\s*=\s*[''"][^''"]+[''"]|jwt\.secret\s*=\s*[''"][^''"]+[''"]'
$search_results = Get-ChildItem -Path "ProductosJSS/src" -Recurse -File -ErrorAction SilentlyContinue | Select-String -Pattern $pattern

if ($search_results) {
    Write-Host "  [FAIL] Se encontraron posibles credenciales harcodeadas en el código:" -ForegroundColor Red
    foreach ($match in $search_results) {
        Write-Host "    $($match.Path):$($match.LineNumber): $($match.Line)" -ForegroundColor Yellow
    }
    $fail = $true
} else {
    Write-Host "  [OK] No se detectaron credenciales expuestas en el código fuente." -ForegroundColor Green
}

Write-Host "====================================================" -ForegroundColor Cyan
if (-not $fail) {
    Write-Host "  >> AUDITORÍA EXITOSA: CUMPLE CON LAS POLÍTICAS <<" -ForegroundColor Green
    Write-Host "====================================================" -ForegroundColor Cyan
    exit 0
} else {
    Write-Host "  >> AUDITORÍA FALLIDA: SE DETECTARON COMPROMISOS <<" -ForegroundColor Red
    Write-Host "====================================================" -ForegroundColor Cyan
    exit 1
}
