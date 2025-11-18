# Guía de Instalación y Uso - Neita's Barber Shop

## 1. Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente en tu sistema:

- **Java 21** (JDK)
- **Maven 3.9+**
- **MySQL 8.0** (o usar Docker)
- **Docker y Docker Compose** (opcional pero recomendado)
- **Git**

## 2. Clonar el Repositorio

```bash
git clone https://github.com/lapaditedubois-tech/sistema-citas-barberia.git
cd sistema-citas-barberia
```

## 3. Configuración de la Base de Datos

### Opción A: Usar Docker (Recomendado)

La forma más sencilla es usar Docker Compose, que levantará automáticamente MySQL y la aplicación:

```bash
docker-compose up --build
```

Esto iniciará:
- MySQL en el puerto 3306
- La aplicación Spring Boot en el puerto 8088

### Opción B: MySQL Local

Si prefieres usar una instalación local de MySQL:

1. Asegúrate de que MySQL esté corriendo en `localhost:3306`
2. Crea la base de datos:

```sql
CREATE DATABASE sistema_citas_neita CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Verifica las credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_citas_neita
spring.datasource.username=root
spring.datasource.password=root
```

## 4. Compilar y Ejecutar la Aplicación

### Con Maven

```bash
# Compilar el proyecto
mvn clean package

# Ejecutar la aplicación
java -jar target/sistema-citas-barberia-1.0.0.jar
```

### Con Maven Wrapper (si está disponible)

```bash
./mvnw spring-boot:run
```

## 5. Acceder a la Aplicación

Una vez que la aplicación esté corriendo:

- **Aplicación Web:** http://localhost:8088
- **API REST:** http://localhost:8088/api
- **Documentación Swagger:** http://localhost:8088/swagger-ui.html
- **API Docs JSON:** http://localhost:8088/api-docs

## 6. Usuarios de Prueba

Al iniciar la aplicación por primera vez, puedes crear usuarios manualmente o usar la API de registro:

### Registro de Usuario (Cliente)

```bash
curl -X POST http://localhost:8088/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "password": "Password123!",
    "telefono": "3001234567"
  }'
```

### Login

```bash
curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "Password123!"
  }'
```

Esto devolverá un token JWT que debes usar en las siguientes peticiones:

```bash
curl -X GET http://localhost:8088/api/servicios/activos \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

## 7. Flujo de Uso del Sistema

### Para Clientes:

1. **Registrarse** en el sistema
2. **Buscar servicios** disponibles
3. **Ver barberos** que ofrecen el servicio deseado
4. **Agendar una cita** seleccionando fecha, hora y barbero
5. **Ver mis citas** programadas
6. **Dejar valoración** después de una cita completada

### Para Profesionales (Barberos):

1. Un administrador debe crear el perfil profesional
2. **Crear servicios** que ofrece con precios personalizados
3. **Gestionar disponibilidad** y horarios
4. **Ver y gestionar citas** asignadas
5. **Subir fotos** a la galería de trabajos
6. **Confirmar/Completar** citas

### Para Administradores:

1. **Gestionar usuarios** (crear, editar, eliminar)
2. **Crear perfiles profesionales**
3. **Gestionar servicios base**
4. **Ver todas las citas** del sistema
5. **Gestionar productos** en venta
6. **Eliminar cualquier registro**

## 8. Endpoints Principales de la API

### Autenticación
- `POST /api/auth/registro` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión

### Servicios
- `GET /api/servicios/activos` - Listar servicios activos
- `GET /api/servicios/{id}` - Obtener servicio por ID
- `POST /api/servicios` - Crear servicio (PROFESIONAL/ADMIN)

### Profesionales
- `GET /api/profesionales/activos` - Listar barberos activos
- `GET /api/profesionales/servicio/{id}` - Barberos que ofrecen un servicio
- `GET /api/profesionales/top-rated` - Barberos mejor calificados

### Citas
- `POST /api/citas` - Crear nueva cita (CLIENTE)
- `GET /api/citas/usuario/{id}` - Mis citas
- `GET /api/citas/profesional/{id}` - Citas del barbero
- `PUT /api/citas/{id}/estado` - Actualizar estado (PROFESIONAL)

### Valoraciones
- `POST /api/valoraciones` - Crear valoración (CLIENTE)
- `GET /api/valoraciones/profesional/{id}` - Ver reseñas de un barbero

## 9. Detener la Aplicación

### Si usas Docker Compose:

```bash
docker-compose down
```

Para eliminar también los volúmenes (base de datos):

```bash
docker-compose down -v
```

### Si ejecutas localmente:

Presiona `Ctrl + C` en la terminal donde está corriendo la aplicación.

## 10. Solución de Problemas

### Error de conexión a MySQL

- Verifica que MySQL esté corriendo
- Revisa las credenciales en `application.properties`
- Asegúrate de que el puerto 3306 no esté ocupado

### Puerto 8088 ya en uso

Cambia el puerto en `application.properties`:

```properties
server.port=8089
```

### Errores de compilación

Asegúrate de tener Java 21 instalado:

```bash
java -version
```

Limpia el proyecto y vuelve a compilar:

```bash
mvn clean install
```

## 11. Próximos Pasos

- Explora la documentación de Swagger para conocer todos los endpoints
- Revisa el archivo `README.md` para entender la arquitectura
- Personaliza los estilos CSS en `src/main/resources/static/css/style.css`
- Implementa el frontend React en la carpeta `frontend/` (opcional)

---

**¿Necesitas ayuda?** Abre un issue en el repositorio de GitHub.
