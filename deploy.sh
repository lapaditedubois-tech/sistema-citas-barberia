#!/bin/bash

# ============================================
# Script de Despliegue Automatizado
# Sistema de Gestión de Citas - Neita's Barber Shop
# ============================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funciones de utilidad
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Banner
echo "============================================"
echo "  Sistema de Citas - Neita's Barber Shop"
echo "  Script de Despliegue Automatizado"
echo "============================================"
echo ""

# Verificar Docker
print_info "Verificando Docker..."
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

print_info "Docker y Docker Compose están instalados ✓"

# Crear directorios necesarios
print_info "Creando directorios necesarios..."
mkdir -p docker/volumes/mysql
mkdir -p docker/volumes/logs
mkdir -p docker/volumes/data
print_info "Directorios creados ✓"

# Verificar archivo .env
if [ ! -f .env ]; then
    print_warning "Archivo .env no encontrado. Creando desde .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        print_info "Archivo .env creado. Por favor revisa y ajusta las variables."
    else
        print_warning "Archivo .env.example no encontrado. Continuando sin .env..."
    fi
fi

# Detener contenedores existentes
print_info "Deteniendo contenedores existentes..."
docker-compose down 2>/dev/null || true

# Limpiar volúmenes huérfanos (opcional)
read -p "¿Deseas limpiar volúmenes huérfanos? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_info "Limpiando volúmenes huérfanos..."
    docker volume prune -f
fi

# Construir imágenes
print_info "Construyendo imágenes Docker..."
docker-compose build --no-cache

# Iniciar servicios
print_info "Iniciando servicios..."
docker-compose up -d

# Esperar a que MySQL esté listo
print_info "Esperando a que MySQL esté listo..."
MAX_TRIES=30
TRIES=0
until docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; do
    TRIES=$((TRIES+1))
    if [ $TRIES -eq $MAX_TRIES ]; then
        print_error "MySQL no está respondiendo después de $MAX_TRIES intentos"
        exit 1
    fi
    echo -n "."
    sleep 2
done
echo ""
print_info "MySQL está listo ✓"

# Esperar a que la aplicación esté lista
print_info "Esperando a que la aplicación esté lista..."
MAX_TRIES=60
TRIES=0
until curl -f http://localhost:8088/actuator/health 2>/dev/null; do
    TRIES=$((TRIES+1))
    if [ $TRIES -eq $MAX_TRIES ]; then
        print_error "La aplicación no está respondiendo después de $MAX_TRIES intentos"
        print_info "Mostrando logs de la aplicación:"
        docker-compose logs app
        exit 1
    fi
    echo -n "."
    sleep 2
done
echo ""
print_info "Aplicación está lista ✓"

# Mostrar información
echo ""
echo "============================================"
print_info "Despliegue completado exitosamente! 🎉"
echo "============================================"
echo ""
echo "Servicios disponibles:"
echo "  - Aplicación:  http://localhost:8088"
echo "  - Swagger:     http://localhost:8088/swagger-ui.html"
echo "  - MySQL:       localhost:3306"
echo ""
echo "Credenciales por defecto:"
echo "  - Admin:       admin@neitabarber.com / admin123"
echo "  - Barbero:     carlos@neitabarber.com / admin123"
echo "  - Cliente:     juan@example.com / admin123"
echo ""
echo "Comandos útiles:"
echo "  - Ver logs:           docker-compose logs -f"
echo "  - Ver logs app:       docker-compose logs -f app"
echo "  - Ver logs MySQL:     docker-compose logs -f mysql"
echo "  - Detener:            docker-compose down"
echo "  - Reiniciar:          docker-compose restart"
echo "  - Estado:             docker-compose ps"
echo ""
echo "Para phpMyAdmin (desarrollo):"
echo "  docker-compose --profile dev up -d phpmyadmin"
echo "  Acceder en: http://localhost:8080"
echo ""
print_info "¡Disfruta del sistema! 🚀"
