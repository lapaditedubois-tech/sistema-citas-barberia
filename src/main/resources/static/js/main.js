// Configuración de la API
const API_URL = 'http://localhost:8088/api';
let authToken = localStorage.getItem('authToken');

// Función para hacer peticiones autenticadas
async function fetchAPI(endpoint, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        }
    };

    if (authToken) {
        defaultOptions.headers['Authorization'] = `Bearer ${authToken}`;
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error en la petición');
    }

    return response.json();
}

// Función de login
async function login(email, password) {
    try {
        const response = await fetchAPI('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });

        authToken = response.token;
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('usuario', JSON.stringify(response));

        return response;
    } catch (error) {
        console.error('Error en login:', error);
        throw error;
    }
}

// Función de registro
async function registro(datos) {
    try {
        const response = await fetchAPI('/auth/registro', {
            method: 'POST',
            body: JSON.stringify(datos)
        });

        authToken = response.token;
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('usuario', JSON.stringify(response));

        return response;
    } catch (error) {
        console.error('Error en registro:', error);
        throw error;
    }
}

// Función de logout
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('usuario');
    authToken = null;
    window.location.href = '/';
}

// Verificar si el usuario está autenticado
function isAuthenticated() {
    return !!authToken;
}

// Obtener usuario actual
function getCurrentUser() {
    const usuario = localStorage.getItem('usuario');
    return usuario ? JSON.parse(usuario) : null;
}

// Cargar servicios
async function cargarServicios() {
    try {
        const servicios = await fetchAPI('/servicios/activos');
        return servicios;
    } catch (error) {
        console.error('Error cargando servicios:', error);
        return [];
    }
}

// Cargar profesionales
async function cargarProfesionales() {
    try {
        const profesionales = await fetchAPI('/profesionales/activos');
        return profesionales;
    } catch (error) {
        console.error('Error cargando profesionales:', error);
        return [];
    }
}

// Crear cita
async function crearCita(datos) {
    try {
        const cita = await fetchAPI('/citas', {
            method: 'POST',
            body: JSON.stringify(datos)
        });
        return cita;
    } catch (error) {
        console.error('Error creando cita:', error);
        throw error;
    }
}

// Formatear fecha
function formatearFecha(fecha) {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Formatear precio
function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(precio);
}

// Mostrar notificación
function mostrarNotificacion(mensaje, tipo = 'info') {
    const notificacion = document.createElement('div');
    notificacion.className = `notificacion notificacion-${tipo}`;
    notificacion.textContent = mensaje;
    
    document.body.appendChild(notificacion);
    
    setTimeout(() => {
        notificacion.remove();
    }, 3000);
}

// Inicialización
document.addEventListener('DOMContentLoaded', () => {
    // Actualizar UI según estado de autenticación
    const usuario = getCurrentUser();
    if (usuario) {
        console.log('Usuario autenticado:', usuario.nombre);
    }
});
