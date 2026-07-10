#!/bin/bash

# Pruebas de aceptacion basicas antes de la entrega
BASE_URL=${1:-"http://localhost:8081"}
echo "Iniciando verificacion en: $BASE_URL"

FAILED=0

# 1. Healthcheck de Actuator
HEALTH_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
if [ "$HEALTH_RESP" -eq 200 ]; then
    echo "Salud del servicio: OK"
else
    echo "Salud del servicio: ERROR (HTTP $HEALTH_RESP)"
    FAILED=1
fi

# 2. Endpoint de Productos
PRODUCTS_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/productos")
if [ "$PRODUCTS_RESP" -eq 200 ]; then
    echo "Endpoint productos: OK"
else
    echo "Endpoint productos: ERROR (HTTP $PRODUCTS_RESP)"
    FAILED=1
fi

# 3. Endpoint de Categorias
CATEGORIES_RESP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/categorias")
if [ "$CATEGORIES_RESP" -eq 200 ]; then
    echo "Endpoint categorias: OK"
else
    echo "Endpoint categorias: ERROR (HTTP $CATEGORIES_RESP)"
    FAILED=1
fi

if [ $FAILED -eq 0 ]; then
    echo "Todas las pruebas pasaron."
    exit 0
else
    echo "Error: algunas pruebas fallaron."
    exit 1
fi
