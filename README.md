# Neita's Barber Shop - Sistema de Gestión de Citas

![Barber Shop](https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=1200)

## 1. Introducción

**Neita's Barber Shop** es un sistema completo de gestión de citas (CRUD) diseñado para barberías modernas que buscan optimizar su operación y ofrecer una experiencia de usuario excepcional. El proyecto está construido con una arquitectura profesional, utilizando tecnologías robustas y siguiendo las mejores prácticas de desarrollo y seguridad.

Este sistema permite a los clientes buscar servicios, elegir a su barbero preferido, y agendar citas de manera sencilla. Por su parte, los barberos pueden gestionar sus servicios, precios y horarios, mientras que los administradores tienen control total sobre la plataforma.

- **Autor:** David Neita
- **Institución:** SENA (Ficha 3063267)
- **Estilo del Proyecto:** Vintage/Clásico con una paleta de colores elegante (negro, dorado, marrón).

## 2. Características Principales

El sistema está diseñado con una clara separación de roles y funcionalidades para garantizar una experiencia de usuario intuitiva y segura.

### 2.1. Roles de Usuario

- **Cliente:**
  - Buscar servicios y ver qué barberos los ofrecen.
  - Agendar, ver y cancelar sus propias citas.
  - Dejar valoraciones (estrellas y comentarios) sobre los servicios recibidos.
  - Ver su historial de citas.

- **Profesional (Barbero):**
  - Gestionar (CRUD) los servicios que ofrece, con precios personalizados.
  - Gestionar su disponibilidad y horario.
  - Confirmar, completar o cancelar las citas agendadas con él.
  - Subir fotos de sus trabajos a una galería personal.

- **Administrador:**
  - Control total del sistema (CRUD completo).
  - Gestionar usuarios, profesionales, servicios base y citas.
  - Eliminar cualquier registro del sistema.
  - Gestionar productos en venta.

### 2.2. Funcionalidades Clave

- **Autenticación y Seguridad:** Sistema de login personalizado con Spring Security y autenticación basada en tokens JWT. Se siguen las recomendaciones de OWASP para proteger contra vulnerabilidades comunes.
- **Búsqueda Avanzada:** Los clientes pueden buscar primero por servicio y luego ver la lista de barberos disponibles que lo ofrecen, junto con sus precios y calificaciones.
- **Gestión de Servicios Personalizados:** Cada barbero puede definir los servicios que ofrece y establecer su propio precio, creando una relación `Muchos a Muchos` entre barberos y servicios.
- **Sistema de Valoraciones:** Los clientes pueden calificar a los barberos después de una cita completada, lo que ayuda a otros usuarios a tomar decisiones informadas.
- **Galería de Trabajos:** Cada barbero tiene una sección para mostrar su portafolio de trabajos.
- **Venta de Productos:** Módulo opcional para que la barbería pueda vender productos (ceras, aceites, etc.).
- **API RESTful:** Endpoints bien definidos y documentados con Swagger (OpenAPI) para una fácil integración con cualquier frontend.
- **Contenerización con Docker:** El proyecto incluye un `Dockerfile` optimizado para producción (usando `jlink` para crear un JRE personalizado) y un archivo `docker-compose.yml` para un despliegue sencillo en entornos de desarrollo y producción.

## 3. Arquitectura y Stack Tecnológico

El proyecto sigue una arquitectura en capas clásica para garantizar la separación de responsabilidades, mantenibilidad y escalabilidad.

**Controlador (API) → Servicio (Lógica de Negocio) → Repositorio (Acceso a Datos)**

### 3.1. Stack Tecnológico

| Componente        | Tecnología Utilizada                                  |
|-------------------|-------------------------------------------------------|
| **Backend**       | Spring Boot 3.5.7                                     |
| **Lenguaje**      | Java 21                                               |
| **Base de Datos** | MySQL 8.0                                             |
| **ORM**           | Spring Data JPA / Hibernate                           |
| **Seguridad**     | Spring Security 6 (con JWT)                           |
| **Vistas (Opcional)** | Thymeleaf                                             |
| **Frontend**      | React + Vite (para consumir la API REST)              |
| **Contenerización** | Docker                                                |
| **Build Tool**    | Maven                                                 |
| **Documentación API** | Springdoc OpenAPI (Swagger)                         |

### 3.2. Modelo de Datos

El modelo de datos fue diseñado para ser flexible y robusto, soportando las funcionalidades clave del sistema. A continuación se muestra el diagrama entidad-relación:

![Diagrama ERD](https://i.imgur.com/URL_DE_LA_IMAGEN_DEL_ERD.png)

**Tablas Principales:**

- `usuario`: Almacena la información de todos los usuarios (clientes, barberos, admins).
- `profesional`: Contiene el perfil extendido de un usuario que es barbero.
- `servicio`: Catálogo de servicios base que pueden ser ofrecidos.
- `barbero_servicio`: Tabla intermedia que conecta a un `profesional` con un `servicio` y define un `precio` personalizado.
- `cita`: Registra la información de una cita (cliente, profesional, servicio, fecha, estado).
- `valoracion`: Almacena las reseñas que los clientes dejan a los profesionales.
- `galeria`: Guarda las imágenes de los trabajos de cada profesional.
- `producto`: Catálogo de productos en venta.

## 4. Configuración y Puesta en Marcha

### 4.1. Prerrequisitos

- Java 21
- Maven 3.9+
- MySQL 8.0
- Docker y Docker Compose (recomendado)

### 4.2. Ejecución con Docker (Recomendado)

La forma más sencilla de levantar todo el entorno es usando Docker Compose. Desde la raíz del proyecto, ejecuta:

```bash
docker-compose up --build
```

Esto construirá la imagen de la aplicación Spring Boot y levantará dos contenedores:
1.  `sistema-citas-mysql`: La base de datos MySQL.
2.  `sistema-citas-app`: La aplicación backend.

La aplicación estará disponible en `http://localhost:8088`.

### 4.3. Ejecución Local (Sin Docker)

1.  **Base de Datos:**
    - Asegúrate de tener una instancia de MySQL corriendo en `localhost:3306`.
    - Crea una base de datos llamada `sistema_citas_neita`.
    - El usuario y contraseña por defecto son `root` y `root`. Puedes cambiar esto en el archivo `application.properties`.

2.  **Backend:**
    - Navega a la raíz del proyecto.
    - Compila y empaqueta la aplicación con Maven:
      ```bash
      mvn clean package
      ```
    - Ejecuta la aplicación:
      ```bash
      java -jar target/sistema-citas-barberia-1.0.0.jar
      ```

### 4.4. Acceso a la Documentación de la API

Una vez que la aplicación esté corriendo, puedes acceder a la documentación interactiva de la API (Swagger UI) en la siguiente URL:

[http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)

## 5. Seguridad

La seguridad es un pilar fundamental de este proyecto. Se han implementado varias medidas basadas en la **lista de verificación de OWASP**:

- **Autenticación Robusta:** Uso de Spring Security con `BCryptPasswordEncoder` para el hashing de contraseñas y JWT para la gestión de sesiones sin estado.
- **Control de Acceso:** Autorización a nivel de método y de endpoint con anotaciones `@PreAuthorize`, garantizando que solo los usuarios con los roles adecuados puedan acceder a los recursos.
- **Validación de Entradas:** Se utiliza `jakarta.validation` en los DTOs para validar todos los datos de entrada y prevenir ataques como Inyección SQL o XSS.
- **Manejo de Excepciones Global:** Un `@ControllerAdvice` centraliza el manejo de errores para evitar la fuga de información sensible del sistema.
- **Protección CSRF:** Deshabilitada para la API REST sin estado, pero se debe tener en cuenta si se implementan formularios web tradicionales.
- **Cabeceras de Seguridad:** Se recomienda agregar cabeceras como `Content-Security-Policy`, `X-Content-Type-Options`, etc., a través de un filtro o en la configuración del proxy inverso en producción.

---
*Este proyecto fue desarrollado con fines educativos como parte del programa del SENA.*
