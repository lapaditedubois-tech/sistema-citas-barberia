# ============================================
# Dockerfile Multi-Stage para Producción
# Sistema de Gestión de Citas - Neita's Barber Shop
# Optimizado con JLink para reducir tamaño de imagen
# ============================================

# ============================================
# Stage 1: Build Stage
# Compila la aplicación con Maven
# ============================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

LABEL maintainer="Neita's Barber Shop"
LABEL description="Build stage para compilar aplicación Spring Boot"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (mejor cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Extraer JAR en capas para mejor cache de Docker
RUN mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf ../*.jar

# ============================================
# Stage 2: JLink Custom JRE
# Crea un JRE personalizado con solo módulos necesarios
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS jlink

# Instalar binutils para jlink
RUN apk add --no-cache binutils

# Analizar dependencias del JAR y crear JRE personalizado
WORKDIR /jlink

# Copiar JAR compilado
COPY --from=builder /app/target/*.jar app.jar

# Crear JRE personalizado con módulos necesarios
RUN jdeps --ignore-missing-deps \
    --print-module-deps \
    --multi-release 21 \
    app.jar > modules.txt && \
    jlink \
    --add-modules $(cat modules.txt),jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre-custom

# ============================================
# Stage 3: Production Runtime
# Imagen final optimizada para producción
# ============================================
FROM alpine:3.19

LABEL maintainer="Neita's Barber Shop"
LABEL description="Sistema de Gestión de Citas - Producción"
LABEL version="1.0.0"

# Variables de entorno para JVM
ENV JAVA_HOME=/opt/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom"

# Instalar dependencias necesarias
RUN apk add --no-cache \
    ca-certificates \
    tzdata \
    curl \
    bash \
    libc6-compat \
    libstdc++ \
    && rm -rf /var/cache/apk/*

# Configurar zona horaria
ENV TZ=America/Bogota
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Crear usuario no-root para seguridad
RUN addgroup -g 1000 spring && \
    adduser -D -u 1000 -G spring spring

# Copiar JRE personalizado
COPY --from=jlink /jre-custom ${JAVA_HOME}

# Crear directorios necesarios
RUN mkdir -p /app/logs /app/data && \
    chown -R spring:spring /app

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR compilado
COPY --from=builder /app/target/*.jar app.jar

# Cambiar permisos
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring

# Exponer puerto de la aplicación
EXPOSE 8088

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8088/actuator/health || exit 1

# Comando de inicio con JVM optimizada
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

# Metadata
LABEL org.opencontainers.image.title="Sistema Citas Barbería"
LABEL org.opencontainers.image.description="Sistema de gestión de citas para barbería"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.vendor="Neita's Barber Shop"
