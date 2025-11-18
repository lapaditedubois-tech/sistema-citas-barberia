-- ============================================
-- Script de Inicialización MySQL
-- Sistema de Gestión de Citas - Neita's Barber Shop
-- ============================================

-- Seleccionar base de datos
USE sistema_citas_neita;

-- ============================================
-- Configuración de zona horaria
-- ============================================
SET time_zone = '-05:00';

-- ============================================
-- Configuración de caracteres
-- ============================================
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ============================================
-- Crear usuario administrador por defecto
-- ============================================
-- Nota: La contraseña 'admin123' está hasheada con BCrypt (12 rounds)
-- Hash generado: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMesbjcYzieGeDCZv4kqvbeZTG

INSERT IGNORE INTO usuario (id, nombre, email, password, telefono, fecha_registro, activo) 
VALUES (
    1,
    'Administrador',
    'admin@neitabarber.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMesbjcYzieGeDCZv4kqvbeZTG',
    '3001234567',
    NOW(),
    TRUE
);

-- Asignar rol de administrador
INSERT IGNORE INTO usuario_roles (usuario_id, roles) 
VALUES (1, 'ADMIN');

-- ============================================
-- Crear barbero profesional de ejemplo
-- ============================================
INSERT IGNORE INTO usuario (id, nombre, email, password, telefono, fecha_registro, activo) 
VALUES (
    2,
    'Carlos Neita',
    'carlos@neitabarber.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMesbjcYzieGeDCZv4kqvbeZTG',
    '3009876543',
    NOW(),
    TRUE
);

-- Asignar rol de profesional
INSERT IGNORE INTO usuario_roles (usuario_id, roles) 
VALUES (2, 'PROFESIONAL');

-- Crear perfil de profesional
INSERT IGNORE INTO profesional (id, especialidad, horario_disponible, activo, usuario_id) 
VALUES (
    1,
    'Cortes clásicos y modernos, diseño de barba',
    '2024-01-01 09:00:00',
    TRUE,
    2
);

-- ============================================
-- Crear servicios de ejemplo
-- ============================================
INSERT IGNORE INTO servicio (id, nombre, descripcion, duracion, precio, activo) 
VALUES 
(1, 'Corte Clásico', 'Corte de cabello tradicional con tijera y máquina', '45 minutos', 25000.00, TRUE),
(2, 'Corte + Barba', 'Corte de cabello completo más arreglo de barba', '60 minutos', 35000.00, TRUE),
(3, 'Afeitado Tradicional', 'Afeitado con navaja y toalla caliente', '30 minutos', 20000.00, TRUE),
(4, 'Diseño de Barba', 'Perfilado y diseño profesional de barba', '30 minutos', 18000.00, TRUE),
(5, 'Corte Niño', 'Corte de cabello para niños hasta 12 años', '30 minutos', 18000.00, TRUE),
(6, 'Corte Premium', 'Corte personalizado con lavado y masaje capilar', '75 minutos', 45000.00, TRUE);

-- ============================================
-- Asignar servicios al barbero
-- ============================================
INSERT IGNORE INTO barbero_servicio (id, precio_personalizado, disponible, profesional_id, servicio_id) 
VALUES 
(1, 25000.00, TRUE, 1, 1),
(2, 35000.00, TRUE, 1, 2),
(3, 20000.00, TRUE, 1, 3),
(4, 18000.00, TRUE, 1, 4),
(5, 18000.00, TRUE, 1, 5),
(6, 45000.00, TRUE, 1, 6);

-- ============================================
-- Crear cliente de ejemplo
-- ============================================
INSERT IGNORE INTO usuario (id, nombre, email, password, telefono, fecha_registro, activo) 
VALUES (
    3,
    'Juan Pérez',
    'juan@example.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMesbjcYzieGeDCZv4kqvbeZTG',
    '3001112233',
    NOW(),
    TRUE
);

-- Asignar rol de cliente
INSERT IGNORE INTO usuario_roles (usuario_id, roles) 
VALUES (3, 'CLIENTE');

-- ============================================
-- Crear cita de ejemplo
-- ============================================
INSERT IGNORE INTO cita (id, fecha_hora, estado, notas, usuario_id, servicio_id, profesional_id) 
VALUES (
    1,
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    'CONFIRMADA',
    'Primera cita del sistema',
    3,
    1,
    1
);

-- ============================================
-- Crear productos de ejemplo
-- ============================================
INSERT IGNORE INTO producto (id, nombre, descripcion, precio, stock, disponible) 
VALUES 
(1, 'Cera para Cabello', 'Fijación fuerte, acabado natural', 35000.00, 15, TRUE),
(2, 'Aceite para Barba', 'Hidrata y suaviza la barba', 28000.00, 20, TRUE),
(3, 'Shampoo Premium', 'Limpieza profunda y nutrición', 32000.00, 12, TRUE),
(4, 'Navaja de Afeitar', 'Acero inoxidable de alta calidad', 45000.00, 8, TRUE),
(5, 'Peine Profesional', 'Resistente al calor', 18000.00, 25, TRUE),
(6, 'Gel Fijador', 'Fijación extra fuerte', 25000.00, 18, TRUE);

-- ============================================
-- Crear imágenes de galería de ejemplo
-- ============================================
INSERT IGNORE INTO galeria (id, titulo, descripcion, url_imagen, visible, profesional_id) 
VALUES 
(1, 'Corte Fade Clásico', 'Degradado perfecto con diseño lateral', 'https://via.placeholder.com/400x400?text=Fade', TRUE, 1),
(2, 'Barba Perfilada', 'Diseño profesional de barba con líneas definidas', 'https://via.placeholder.com/400x400?text=Barba', TRUE, 1),
(3, 'Corte Moderno', 'Estilo contemporáneo con textura', 'https://via.placeholder.com/400x400?text=Moderno', TRUE, 1);

-- ============================================
-- Crear valoraciones de ejemplo
-- ============================================
INSERT IGNORE INTO valoracion (id, calificacion, comentario, fecha, usuario_id, profesional_id) 
VALUES 
(1, 5, 'Excelente servicio, muy profesional y atento', NOW(), 3, 1);

-- ============================================
-- Índices adicionales para optimización
-- ============================================
CREATE INDEX IF NOT EXISTS idx_cita_fecha ON cita(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_cita_estado ON cita(estado);
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
CREATE INDEX IF NOT EXISTS idx_servicio_activo ON servicio(activo);
CREATE INDEX IF NOT EXISTS idx_profesional_activo ON profesional(activo);

-- ============================================
-- Mensaje de confirmación
-- ============================================
SELECT 'Base de datos inicializada correctamente' AS mensaje;
SELECT COUNT(*) AS total_usuarios FROM usuario;
SELECT COUNT(*) AS total_servicios FROM servicio;
SELECT COUNT(*) AS total_profesionales FROM profesional;
SELECT COUNT(*) AS total_citas FROM cita;
