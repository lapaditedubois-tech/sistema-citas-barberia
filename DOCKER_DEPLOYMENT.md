# Guía de Despliegue con Docker

## Sistema de Gestión de Citas - Neita's Barber Shop

Esta guía proporciona instrucciones detalladas para desplegar el sistema usando Docker y Docker Compose en producción.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Arquitectura Docker](#arquitectura-docker)
3. [Configuración](#configuración)
4. [Despliegue Rápido](#despliegue-rápido)
5. [Despliegue Manual](#despliegue-manual)
6. [Verificación](#verificación)
7. [Gestión de Contenedores](#gestión-de-contenedores)
8. [Backup y Restauración](#backup-y-restauración)
9. [Optimizaciones](#optimizaciones)
10. [Troubleshooting](#troubleshooting)

---

## 🔧 Requisitos Previos

### Software Necesario

- **Docker**: versión 20.10 o superior
- **Docker Compose**: versión 2.0 o superior
- **Git**: para clonar el repositorio
- **4GB RAM mínimo** (recomendado 8GB)
- **20GB espacio en disco**

### Instalación de Docker

#### Ubuntu/Debian

```bash
# Actualizar repositorios
sudo apt-get update

# Instalar dependencias
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# Agregar clave GPG de Docker
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Agregar repositorio
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER

# Reiniciar sesión o ejecutar
newgrp docker
```

#### CentOS/RHEL

```bash
# Instalar dependencias
sudo yum install -y yum-utils

# Agregar repositorio
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# Instalar Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Iniciar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
```

### Verificar Instalación

```bash
docker --version
docker compose version
```

---

## 🏗️ Arquitectura Docker

### Contenedores

El sistema utiliza una arquitectura multi-contenedor:

```
┌─────────────────────────────────────────────┐
│           sistema-citas-app                 │
│       (Spring Boot Application)             │
│         Puerto: 8088                        │
│         JRE: Custom JLink (Java 21)         │
└─────────────────┬───────────────────────────┘
                  │
                  │ JDBC Connection
                  │
┌─────────────────▼───────────────────────────┐
│         sistema-citas-mysql                 │
│            (MySQL 8.0.36)                   │
│         Puerto: 3306                        │
│         Volumen: mysql_data                 │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│      sistema-citas-phpmyadmin (opcional)    │
│           (phpMyAdmin 5.2.1)                │
│         Puerto: 8080                        │
│         Profile: dev                        │
└─────────────────────────────────────────────┘
```

### Volúmenes

| Volumen | Propósito | Ubicación |
|---------|-----------|-----------|
| `mysql_data` | Datos de MySQL | `/var/lib/mysql` |
| `app_logs` | Logs de aplicación | `/app/logs` |
| `app_data` | Datos de aplicación | `/app/data` |

### Red

- **Red**: `sistema-citas-network` (bridge)
- **Subnet**: `172.20.0.0/16`

---

## ⚙️ Configuración

### 1. Clonar Repositorio

```bash
git clone https://github.com/lapaditedubois-tech/sistema-citas-barberia.git
cd sistema-citas-barberia
```

### 2. Configurar Variables de Entorno

```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar variables (opcional)
nano .env
```

#### Variables Importantes

```env
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=sistema_citas_neita
MYSQL_USER=neita_user
MYSQL_PASSWORD=neita_password_2024

# JWT
JWT_SECRET=neita-barber-shop-secret-key-2024-production-ultra-secure
JWT_EXPIRATION=86400000

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_NEITA=DEBUG
```

### 3. Crear Directorios

```bash
mkdir -p docker/volumes/mysql
mkdir -p docker/volumes/logs
mkdir -p docker/volumes/data
```

---

## 🚀 Despliegue Rápido

### Opción 1: Script Automatizado (Recomendado)

```bash
# Ejecutar script de despliegue
./deploy.sh
```

El script realizará:
- ✅ Verificación de Docker
- ✅ Creación de directorios
- ✅ Construcción de imágenes
- ✅ Inicio de servicios
- ✅ Verificación de salud
- ✅ Muestra de información

### Opción 2: Docker Compose Directo

```bash
# Construir y levantar servicios
docker-compose up -d --build

# Ver logs
docker-compose logs -f
```

---

## 🔨 Despliegue Manual

### Paso 1: Construir Imagen

```bash
# Construir imagen de la aplicación
docker-compose build --no-cache app
```

**Detalles del Build:**
- **Stage 1**: Compilación con Maven (descarga dependencias, compila código)
- **Stage 2**: Creación de JRE personalizado con JLink (reduce tamaño ~60%)
- **Stage 3**: Imagen final optimizada con Alpine Linux

**Tiempo estimado**: 5-10 minutos (primera vez)

### Paso 2: Iniciar MySQL

```bash
# Iniciar solo MySQL
docker-compose up -d mysql

# Verificar salud
docker-compose ps mysql
docker-compose logs mysql
```

**Esperar hasta ver:**
```
mysql_1  | [Server] /usr/sbin/mysqld: ready for connections
```

### Paso 3: Iniciar Aplicación

```bash
# Iniciar aplicación
docker-compose up -d app

# Ver logs en tiempo real
docker-compose logs -f app
```

**Esperar hasta ver:**
```
app_1    | Started SistemaCitasBarberiaApplication in X.XXX seconds
```

### Paso 4: Verificar Servicios

```bash
# Ver estado de todos los servicios
docker-compose ps

# Verificar salud de la aplicación
curl http://localhost:8088/actuator/health
```

---

## ✅ Verificación

### 1. Verificar Contenedores

```bash
# Listar contenedores
docker-compose ps

# Salida esperada:
# NAME                    STATUS              PORTS
# sistema-citas-app       Up (healthy)        0.0.0.0:8088->8088/tcp
# sistema-citas-mysql     Up (healthy)        0.0.0.0:3306->3306/tcp
```

### 2. Verificar Logs

```bash
# Logs de aplicación
docker-compose logs app | tail -50

# Logs de MySQL
docker-compose logs mysql | tail -50

# Logs en tiempo real
docker-compose logs -f
```

### 3. Verificar Endpoints

```bash
# Health check
curl http://localhost:8088/actuator/health

# Respuesta esperada:
# {"status":"UP"}

# Swagger UI
curl -I http://localhost:8088/swagger-ui.html

# Página principal
curl -I http://localhost:8088/
```

### 4. Verificar Base de Datos

```bash
# Conectar a MySQL
docker-compose exec mysql mysql -uroot -proot sistema_citas_neita

# Dentro de MySQL:
SHOW TABLES;
SELECT COUNT(*) FROM usuario;
SELECT COUNT(*) FROM servicio;
EXIT;
```

### 5. Probar Login

```bash
# Probar endpoint de login
curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@neitabarber.com","password":"admin123"}'
```

---

## 🎛️ Gestión de Contenedores

### Comandos Básicos

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Reiniciar servicios
docker-compose restart

# Reiniciar servicio específico
docker-compose restart app

# Ver logs
docker-compose logs -f [servicio]

# Ver estado
docker-compose ps

# Ver uso de recursos
docker stats
```

### Acceso a Contenedores

```bash
# Shell en contenedor de aplicación
docker-compose exec app sh

# Shell en contenedor de MySQL
docker-compose exec mysql bash

# Ejecutar comando en contenedor
docker-compose exec app java -version
```

### Actualizar Aplicación

```bash
# Detener aplicación
docker-compose stop app

# Reconstruir imagen
docker-compose build --no-cache app

# Iniciar aplicación
docker-compose up -d app

# Ver logs
docker-compose logs -f app
```

### Limpiar Sistema

```bash
# Detener y eliminar contenedores
docker-compose down

# Eliminar volúmenes (¡CUIDADO! Borra datos)
docker-compose down -v

# Eliminar imágenes
docker-compose down --rmi all

# Limpiar sistema completo
docker system prune -a --volumes
```

---

## 💾 Backup y Restauración

### Backup de Base de Datos

```bash
# Crear backup
docker-compose exec mysql mysqldump \
  -uroot -proot \
  --databases sistema_citas_neita \
  --add-drop-database \
  --routines \
  --triggers \
  --events \
  > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup comprimido
docker-compose exec mysql mysqldump \
  -uroot -proot \
  --databases sistema_citas_neita \
  | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

### Restaurar Base de Datos

```bash
# Desde archivo SQL
docker-compose exec -T mysql mysql \
  -uroot -proot \
  < backup_20240101_120000.sql

# Desde archivo comprimido
gunzip < backup_20240101_120000.sql.gz | \
  docker-compose exec -T mysql mysql -uroot -proot
```

### Backup de Volúmenes

```bash
# Backup de volumen MySQL
docker run --rm \
  -v sistema-citas-barberia_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql_data_backup.tar.gz -C /data .

# Backup de logs
docker run --rm \
  -v sistema-citas-barberia_app_logs:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/app_logs_backup.tar.gz -C /data .
```

### Restaurar Volúmenes

```bash
# Restaurar volumen MySQL
docker run --rm \
  -v sistema-citas-barberia_mysql_data:/data \
  -v $(pwd):/backup \
  alpine sh -c "cd /data && tar xzf /backup/mysql_data_backup.tar.gz"
```

---

## ⚡ Optimizaciones

### 1. Tamaño de Imagen

La imagen final está optimizada:

- **Multi-stage build**: Separa build de runtime
- **JLink**: JRE personalizado (~60% más pequeño)
- **Alpine Linux**: Base mínima (~5MB)
- **Tamaño final**: ~200MB (vs ~500MB sin optimizar)

### 2. Rendimiento JVM

Variables configuradas en `docker-compose.yml`:

```yaml
JAVA_OPTS: >-
  -XX:+UseContainerSupport          # Detecta límites de contenedor
  -XX:MaxRAMPercentage=75.0         # Usa 75% de RAM disponible
  -XX:InitialRAMPercentage=50.0     # Inicia con 50% de RAM
  -XX:+UseG1GC                      # Garbage Collector G1
  -XX:+UseStringDeduplication       # Optimiza strings
  -XX:+OptimizeStringConcat         # Optimiza concatenación
```

### 3. MySQL Performance

Configuraciones en `docker/mysql/my.cnf`:

```ini
innodb_buffer_pool_size = 512M    # Cache de InnoDB
max_connections = 200              # Conexiones simultáneas
innodb_log_file_size = 128M       # Tamaño de logs
```

### 4. Health Checks

Configurados para monitoreo automático:

```yaml
# Aplicación
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8088/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 90s

# MySQL
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 30s
```

---

## 🐛 Troubleshooting

### Problema: Aplicación no inicia

**Síntomas:**
```
app_1  | Error: Unable to connect to database
```

**Solución:**
```bash
# Verificar que MySQL esté corriendo
docker-compose ps mysql

# Verificar logs de MySQL
docker-compose logs mysql

# Reiniciar MySQL
docker-compose restart mysql

# Esperar 30 segundos y reiniciar app
sleep 30
docker-compose restart app
```

### Problema: Puerto 8088 ya en uso

**Síntomas:**
```
Error: Bind for 0.0.0.0:8088 failed: port is already allocated
```

**Solución:**
```bash
# Encontrar proceso usando el puerto
sudo lsof -i :8088
# o
sudo netstat -tulpn | grep 8088

# Matar proceso
sudo kill -9 [PID]

# O cambiar puerto en docker-compose.yml
ports:
  - "8089:8088"  # Usar puerto 8089 en host
```

### Problema: MySQL no acepta conexiones

**Síntomas:**
```
Communications link failure
```

**Solución:**
```bash
# Verificar estado de MySQL
docker-compose exec mysql mysqladmin ping -h localhost -uroot -proot

# Si falla, revisar logs
docker-compose logs mysql | grep ERROR

# Recrear contenedor
docker-compose down
docker-compose up -d mysql
```

### Problema: Falta de espacio en disco

**Síntomas:**
```
no space left on device
```

**Solución:**
```bash
# Ver uso de disco de Docker
docker system df

# Limpiar imágenes no usadas
docker image prune -a

# Limpiar contenedores detenidos
docker container prune

# Limpiar volúmenes no usados
docker volume prune

# Limpiar todo (¡CUIDADO!)
docker system prune -a --volumes
```

### Problema: Build muy lento

**Solución:**
```bash
# Usar cache de Maven
docker-compose build --build-arg MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"

# Aumentar recursos de Docker
# Docker Desktop > Settings > Resources
# RAM: 4GB mínimo
# CPU: 2 cores mínimo
```

### Problema: Logs no aparecen

**Solución:**
```bash
# Verificar configuración de logging
docker-compose exec app cat /app/logs/application.log

# Verificar permisos
docker-compose exec app ls -la /app/logs

# Reiniciar con logs en consola
docker-compose up app
```

---

## 📊 Monitoreo

### Métricas con Actuator

```bash
# Health
curl http://localhost:8088/actuator/health

# Info
curl http://localhost:8088/actuator/info

# Metrics
curl http://localhost:8088/actuator/metrics

# Prometheus
curl http://localhost:8088/actuator/prometheus
```

### Logs Centralizados

```bash
# Ver todos los logs
docker-compose logs -f

# Filtrar por servicio
docker-compose logs -f app

# Últimas 100 líneas
docker-compose logs --tail=100 app

# Desde timestamp
docker-compose logs --since 2024-01-01T00:00:00
```

---

## 🔒 Seguridad

### Cambiar Credenciales

**Antes de producción, cambiar:**

1. **MySQL root password**
   ```yaml
   MYSQL_ROOT_PASSWORD: [password-seguro]
   ```

2. **JWT Secret**
   ```yaml
   JWT_SECRET: [secret-key-ultra-seguro-256-bits]
   ```

3. **Usuario MySQL**
   ```yaml
   MYSQL_USER: [usuario-custom]
   MYSQL_PASSWORD: [password-seguro]
   ```

### Firewall

```bash
# Permitir solo puertos necesarios
sudo ufw allow 8088/tcp  # Aplicación
sudo ufw allow 3306/tcp  # MySQL (solo si acceso externo)
sudo ufw enable
```

### HTTPS con Nginx

Agregar servicio nginx en `docker-compose.yml`:

```yaml
nginx:
  image: nginx:alpine
  ports:
    - "443:443"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf:ro
    - ./ssl:/etc/nginx/ssl:ro
  depends_on:
    - app
```

---

## 📚 Recursos Adicionales

- [Documentación Docker](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)

---

## 📞 Soporte

Para problemas o preguntas:
- **GitHub Issues**: https://github.com/lapaditedubois-tech/sistema-citas-barberia/issues
- **Email**: support@neitabarber.com

---

**¡Despliegue exitoso! 🎉**
