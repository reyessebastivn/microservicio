#!/bin/bash

# ─────────────────────────────────────────────────────────────
#  Pruebas de Aceptación Automatizadas (Entorno Simulado)
#  Valida los endpoints principales antes de aprobar despliegue
# ─────────────────────────────────────────────────────────────

BASE_URL=${1:-"http://localhost:8081"}
echo "Iniciando pruebas de aceptación contra: $BASE_URL"

FAILED=0

# 1. Validar endpoint de Actuator Health
echo "Prueba 1: Verificando estado del servicio (/actuator/health)..."
HEALTH_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")

if [ "$HEALTH_RESP" -eq 200 ]; then
    echo "  [OK] El servicio está arriba y saludable (HTTP 200)."
else
    echo "  [FAIL] El servicio no está disponible. HTTP: $HEALTH_RESP"
    FAILED=1
fi

# 2. Validar endpoint de Productos (GET /api/productos)
echo "Prueba 2: Consultando lista de productos (GET /api/productos)..."
PRODUCTS_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/productos")

if [ "$PRODUCTS_RESP" -eq 200 ]; then
    echo "  [OK] Endpoint de productos funcionando correctamente (HTTP 200)."
else
    echo "  [FAIL] Error al consultar productos. HTTP: $PRODUCTS_RESP"
    FAILED=1
fi

# 3. Validar endpoint de Categorías (GET /api/categorias)
echo "Prueba 3: Consultando lista de categorías (GET /api/categorias)..."
CATEGORIES_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/categorias")

if [ "$CATEGORIES_RESP" -eq 200 ]; then
    echo "  [OK] Endpoint de categorías funcionando correctamente (HTTP 200)."
else
    echo "  [FAIL] Error al consultar categorías. HTTP: $CATEGORIES_RESP"
    FAILED=1
fi

echo "===================================================="
if [ $FAILED -eq 0 ]; then
    echo "  >> TODAS LAS PRUEBAS DE ACEPTACIÓN PASARON CON ÉXITO <<"
    echo "===================================================="
    exit 0
else
    echo "  >> ALGUNAS PRUEBAS DE ACEPTACIÓN FALLARON <<"
    echo "===================================================="
    exit 1
fi
