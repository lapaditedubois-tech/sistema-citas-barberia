# Etapa 1: Build con Maven
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Crear imagen JRE personalizada con jlink
FROM eclipse-temurin:21-jdk-alpine AS jlink

# Crear un JRE personalizado con solo los módulos necesarios
RUN jlink \
    --add-modules java.base,java.logging,java.xml,java.naming,java.desktop,java.management,java.sql,java.instrument \
    --add-modules jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre-custom

# Etapa 3: Imagen final optimizada
FROM alpine:3.19

# Instalar dependencias necesarias
RUN apk add --no-cache \
    libc6-compat \
    libstdc++ \
    && addgroup -g 1001 -S appgroup \
    && adduser -u 1001 -S appuser -G appgroup

# Copiar JRE personalizado
COPY --from=jlink /jre-custom /opt/jre

# Copiar JAR de la aplicación
COPY --from=build /app/target/*.jar /app/app.jar

# Cambiar propietario de los archivos
RUN chown -R appuser:appgroup /app

# Cambiar a usuario no root
USER appuser

WORKDIR /app

# Variables de entorno
ENV JAVA_HOME=/opt/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Exponer puerto
EXPOSE 8088

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8088/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
