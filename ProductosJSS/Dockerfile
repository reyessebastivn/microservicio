# ── Etapa 1: Compilación ────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ── Etapa 2: Imagen de ejecución (más liviana) ───────────────
FROM eclipse-temurin:21-jre

WORKDIR /app

# curl es necesario para el healthcheck en docker-compose
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
