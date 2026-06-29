#!/bin/bash

# ─────────────────────────────────────────────────────────────
#  Script de Auditoría de Cumplimiento Técnico
#  Verifica políticas básicas de seguridad y calidad antes de compilar
# ─────────────────────────────────────────────────────────────

echo "===================================================="
echo "       INICIO DE AUDITORÍA DE CUMPLIMIENTO          "
echo "===================================================="

# Navegar a la raíz del repositorio Git para asegurar el contexto correcto
cd "$(git rev-parse --show-toplevel)" || exit 1

FAIL=0

# 1. Verificar que .env esté en .gitignore
echo "[AUDIT] 1. Verificando que .env esté en .gitignore..."
if tr -d '\r' < .gitignore 2>/dev/null | grep -q "^\.env$"; then
    echo "  [OK] .env está correctamente configurado en .gitignore."
else
    echo "  [FAIL] .env no se encuentra en .gitignore!"
    FAIL=1
fi

# 2. Verificar que no haya secretos expuestos en código
echo "[AUDIT] 2. Buscando posibles credenciales harcodeadas..."

# Buscar asignaciones de password o secret en archivos de código java/properties
SEARCH_RESULTS=$(grep -rnEi "password\s*=\s*['\"][^'\"]+['\"]|jwt\.secret\s*=\s*['\"][^'\"]+['\"]" ProductosJSS/src/ 2>/dev/null)

if [ -n "$SEARCH_RESULTS" ]; then
    echo "  [FAIL] Se encontraron posibles credenciales harcodeadas en el código:"
    echo "$SEARCH_RESULTS"
    FAIL=1
else
    echo "  [OK] No se detectaron credenciales expuestas en el código fuente."
fi

echo "===================================================="
if [ $FAIL -eq 0 ]; then
    echo "  >> AUDITORÍA EXITOSA: CUMPLE CON LAS POLÍTICAS <<"
    echo "===================================================="
    exit 0
else
    echo "  >> AUDITORÍA FALLIDA: SE DETECTARON COMPROMISOS <<"
    echo "===================================================="
    exit 1
fi
