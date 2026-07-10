#!/bin/bash

# ─────────────────────────────────────────────────────────────
#  Push Metrics to Prometheus Pushgateway
#  Envía estadísticas de build y cobertura al Pushgateway
# ─────────────────────────────────────────────────────────────

PUSHGATEWAY_URL=${1:-"http://localhost:9091"}
DURATION=${2:-"0"}
STATUS=${3:-"1"} # 1 success, 0 failure
BUGS=${4:-"0"}
VULNS=${5:-"0"}

echo "Enviando métricas de CI/CD a Pushgateway en: $PUSHGATEWAY_URL"

# Intentar extraer cobertura de JaCoCo
JACOCO_FILE="ProductosJSS/target/site/jacoco/jacoco.xml"
COVERAGE=0
if [ -f "$JACOCO_FILE" ]; then
    echo "Leyendo cobertura desde $JACOCO_FILE..."
    LINE_COVERAGE_DATA=$(grep -h '<counter type="LINE"' "$JACOCO_FILE" | tail -n 1)
    if [ -n "$LINE_COVERAGE_DATA" ]; then
        MISSED=$(echo "$LINE_COVERAGE_DATA" | sed -E 's/.*missed="([0-9]+)".*/\1/')
        COVERED=$(echo "$LINE_COVERAGE_DATA" | sed -E 's/.*covered="([0-9]+)".*/\1/')
        if [ -n "$MISSED" ] && [ -n "$COVERED" ] && [ $((MISSED + COVERED)) -gt 0 ]; then
            COVERAGE=$(echo "scale=2; ($COVERED * 100) / ($MISSED + COVERED)" | bc -l 2>/dev/null || awk "BEGIN {print ($COVERED * 100) / ($MISSED + COVERED)}")
        fi
    fi
fi

echo "Cobertura calculada: $COVERAGE%"

# Enviar métricas mediante curl
cat <<EOF | curl --data-binary @- "$PUSHGATEWAY_URL/metrics/job/cicd_pipeline"
# HELP cicd_build_duration_seconds Duración del proceso de compilación y pruebas en segundos
# TYPE cicd_build_duration_seconds gauge
cicd_build_duration_seconds $DURATION

# HELP cicd_pipeline_status Estado de finalización del pipeline (1=Éxito, 0=Fallo)
# TYPE cicd_pipeline_status gauge
cicd_pipeline_status $STATUS

# HELP cicd_sonar_coverage Cobertura de código reportada por JaCoCo/SonarCloud
# TYPE cicd_sonar_coverage gauge
cicd_sonar_coverage $COVERAGE

# HELP cicd_sonar_bugs Cantidad de bugs reportados por SonarQube
# TYPE cicd_sonar_bugs gauge
cicd_sonar_bugs $BUGS

# HELP cicd_sonar_vulnerabilities Cantidad de vulnerabilidades detectadas en dependencias/código
# TYPE cicd_sonar_vulnerabilities gauge
cicd_sonar_vulnerabilities $VULNS
EOF

echo "Métricas enviadas exitosamente."
