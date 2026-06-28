# Microservicio ProductosJSS

Este proyecto es un microservicio desarrollado con Spring Boot y Java 21 que permite gestionar productos, categorías, usuarios y órdenes de compra. Fue desarrollado como base para el trabajo de DevOps e incluye autenticación con JWT, documentación de API con Swagger, pruebas unitarias y un pipeline de CI/CD con GitHub Actions.

---

## Cómo levantar el proyecto localmente

Para correr el proyecto necesitas tener Docker Desktop instalado.

### Pasos

**1. Clonar el repositorio**

```bash
git clone https://github.com/reyessebastivn/microservicio.git
cd microservicio
```

**2. Crear el archivo de variables de entorno**

El proyecto usa un archivo `.env` para las credenciales. Hay una plantilla lista para copiar:

```bash
# En Linux/Mac:
cp .env.example .env

# En Windows (PowerShell):
Copy-Item .env.example .env
```

Los valores por defecto que vienen en `.env.example` funcionan para desarrollo local, no es necesario cambiarlos.

**3. Levantar el stack**

```bash
docker compose up --build
```

Esto levanta dos contenedores: la base de datos MySQL y la aplicación Spring Boot.

| Servicio | Dirección |
|----------|-----------|
| API REST | http://localhost:8081 |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| MySQL | localhost:3307 |

Para detener todo:

```bash
docker compose down
```

Si también quieres borrar los datos guardados en el volumen:

```bash
docker compose down -v
```

---

## Estructura del proyecto

```
microservicio/
├── .github/
│   ├── workflows/ci.yml      # Pipeline de GitHub Actions
│   └── dependabot.yml        # Actualizacion automatica de dependencias
├── .env.example              # Plantilla de variables de entorno
├── docker-compose.yml        # Orquestacion del stack
└── ProductosJSS/
    ├── Dockerfile            # Imagen del microservicio
    ├── pom.xml               # Dependencias Maven
    └── src/
        ├── main/java/        # Codigo fuente
        └── test/java/        # Pruebas unitarias
```

El `docker-compose.yml` incluye redes personalizadas, volúmenes persistentes, variables de entorno, healthchecks y dependencias entre servicios.

---

## Pipeline CI/CD

El pipeline se ejecuta automáticamente con cada push o pull request a `main` y está dividido en 3 etapas que corren en orden:

### Etapa 1: Build, pruebas y análisis de seguridad

- Se compila el proyecto y se ejecutan las pruebas unitarias con JUnit
- Se genera el reporte de cobertura con JaCoCo
- Se hace análisis estático del código con SpotBugs (SAST) — bloquea si encuentra bugs de severidad alta
- Se escanean las dependencias con Snyk (SCA) — bloquea si encuentra vulnerabilidades altas
- Se hace un análisis adicional con OWASP Dependency Check
- Se empaqueta el `.jar` y se guarda como artefacto

### Etapa 2: Construcción de imagen Docker

- Descarga el `.jar` de la etapa anterior
- Construye la imagen Docker usando el Dockerfile del proyecto
- Verifica que la imagen fue creada correctamente

### Etapa 3: Despliegue en entorno simulado (solo en push a main)

- Crea el archivo `.env` desde los secrets configurados en GitHub
- Levanta el stack completo con `docker compose up --build -d`
- Hace un health check para confirmar que la app responde correctamente
- Muestra los logs y baja el stack al terminar

### Cómo garantizamos la trazabilidad y calidad

Cada ejecución del pipeline queda registrada en GitHub Actions vinculada al commit exacto que la disparó. Cualquier cambio en el código pasa obligatoriamente por pruebas, análisis de seguridad y construcción de imagen antes de llegar al despliegue. Si alguna de las etapas falla, las siguientes no se ejecutan.

Las herramientas de seguridad (Snyk y SpotBugs) están configuradas para bloquear el pipeline cuando detectan problemas de severidad alta, evitando que código vulnerable llegue al despliegue. Además, Dependabot revisa semanalmente las dependencias del proyecto y abre pull requests automáticos cuando hay actualizaciones disponibles.

### Secrets necesarios en GitHub

Para que el pipeline funcione hay que configurar estos secrets en **Settings → Secrets and variables → Actions**:

| Secret | Para qué sirve |
|--------|----------------|
| `SNYK_TOKEN` | Token de Snyk (se obtiene gratis en app.snyk.io) |
| `MYSQL_ROOT_PASSWORD` | Contraseña de MySQL para el entorno de CI |
| `MYSQL_DATABASE` | Nombre de la base de datos |
| `JWT_SECRET` | Clave para firmar los tokens JWT |

---

## Pruebas unitarias

Las pruebas están en `src/test/java/` y cubren los servicios principales de la aplicación:

- `ProductoServiceTest`
- `CategoriaServiceTest`
- `UsuarioServiceTest`

Para ejecutarlas localmente:

```bash
cd ProductosJSS
mvn clean test
```

---

## Seguridad

- La autenticación usa tokens JWT firmados con una clave secreta
- Las contraseñas se almacenan hasheadas con BCrypt
- Las dependencias se escanean automáticamente en cada ejecución del pipeline

---

## Flujo de trabajo con Git (GitFlow)

Se usó GitFlow como estrategia de ramas:

- `main` → versión estable
- `develop` → integración de cambios
- `feature/*` → nuevas funcionalidades
- `hotfix/*` → correcciones urgentes

---

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `MYSQL_ROOT_PASSWORD` | Contraseña de MySQL |
| `MYSQL_ROOT_USERNAME` | Usuario de MySQL (default: root) |
| `MYSQL_DATABASE` | Nombre de la base de datos (default: basejoyas) |
| `JWT_SECRET` | Clave secreta para JWT |

---

## Entrega N°3: Monitoreo, Cumplimiento y Calidad de Código

Este apartado describe la integración de las herramientas de monitoreo (Prometheus y Grafana), herramientas de calidad/cumplimiento (SonarCloud, Snyk y auditoría personalizada) y cómo estas herramientas forman parte del pipeline de CI/CD.

### 1. Arquitectura de Monitoreo (Prometheus y Grafana)

Hemos configurado un stack de monitoreo autocontenido y orquestado mediante Docker Compose para visualizar el estado, rendimiento y disponibilidad de la aplicación en tiempo real:

* **Spring Boot Actuator:** El microservicio exporta métricas en el formato nativo de Prometheus a través del endpoint público `/actuator/prometheus`, configurado con las dependencias de Micrometer.
* **Prometheus:** Recolecta las métricas de la aplicación cada 15 segundos y las almacena de forma temporal.
* **Grafana:** Se conecta a Prometheus como origen de datos y visualiza la información en un panel interactivo preconfigurado.

#### Métricas clave del Dashboard:
* **Uso de Recursos (CPU y Memoria JVM):** Permite detectar fugas de memoria o saturación del procesador, ayudando a decidir si el servicio necesita escalamiento horizontal (más instancias) o vertical (más recursos).
* **Tasa de Errores HTTP (2xx, 4xx, 5xx):** Monitorea la estabilidad de los endpoints. Un pico en los errores 5xx indica fallas internas críticas que requieren inspección inmediata en el código o en la base de datos.
* **Tiempo de Actividad (Uptime):** Indica la disponibilidad del servicio. Reinicios no planificados o caídas de uptime alertan sobre inestabilidad de la aplicación.
* **Volumen de Tráfico (Throughput):** Registra el total de solicitudes procesadas para entender la carga real del sistema.

---

### 2. Políticas de Cumplimiento Técnico y Calidad

Para asegurar la robustez, seguridad y cumplimiento del código, se implementan tres niveles de calidad automatizados en el pipeline de CI/CD:

1. **Auditoría de Cumplimiento Técnico (`audit-compliance.sh`):**
   * Un script que se ejecuta al inicio del pipeline para verificar la higiene de Git.
   * Valida que no existan archivos con secretos o contraseñas expuestas en texto plano y confirma que el archivo `.env` esté debidamente ignorado en `.gitignore`.
2. **Análisis de Vulnerabilidades (Snyk - SCA):**
   * Escanea las dependencias de Maven. Si detecta alguna librería externa con vulnerabilidades críticas o de severidad alta, el pipeline se detiene de inmediato.
3. **Análisis de Calidad Estática (SonarCloud):**
   * Evalúa la calidad interna del software: porcentaje de cobertura de pruebas unitarias (JaCoCo), duplicación de código, bugs potenciales y deudas técnicas.
   * Integrado en la fase de construcción de GitHub Actions.

---

### 3. Evidencia de Ejecución Local y Accesos

#### Direcciones Locales de los Servicios:
* **API del Microservicio:** http://localhost:8081
* **Swagger UI:** http://localhost:8081/swagger-ui.html
* **Métricas Actuator:** http://localhost:8081/actuator/prometheus
* **Prometheus UI:** http://localhost:9090
* **Grafana Dashboard:** http://localhost:3000
  * **Credenciales de Grafana:** Usuario: `admin` | Contraseña: `admin` (la primera vez te solicitará cambiarla, puedes omitirlo). El dashboard titulado *"Dashboard de Monitoreo - ProductosJSS"* se carga automáticamente.

#### Nuevos Secrets Necesarios en GitHub:
Para habilitar el análisis en la nube de SonarCloud, debes agregar este secret en tu repositorio:
* `SONAR_TOKEN`: Token generado desde tu cuenta de SonarCloud para el proyecto.

