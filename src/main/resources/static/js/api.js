// Función auxiliar global para sanitizar HTML
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

const API_BASE = '/api';

const Api = {
  // Guarda los datos del usuario autenticado en localStorage
  guardarSesion(usuario) {
    localStorage.setItem('usuario', JSON.stringify(usuario));
  },

  // Retorna el objeto usuario guardado en la sesión
  usuario() {
    const user = localStorage.getItem('usuario');
    return user ? JSON.parse(user) : null;
  },

  obtenerUsuario() {
    return this.usuario();
  },

  estaLogueado() {
    return this.usuario() !== null;
  },

  tieneRol(...roles) {
    const user = this.usuario();
    return user && roles.includes(user.rol);
  },

  cerrarSesion() {
    localStorage.removeItem('usuario');
  },

  async login(param1, param2) {
    let credenciales = {};
    if (typeof param1 === 'object' && param1 !== null) {
      credenciales = param1;
    } else {
      credenciales = { email: param1, password: param2 };
    }

    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credenciales)
    });

    if (!res.ok) {
      throw new Error('Credenciales inválidas');
    }

    return await res.json();
  },

  async registrar(datosUsuario) {
  const res = await fetch(`${API_BASE}/auth/registro`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datosUsuario)
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.mensaje || 'No se pudo completar el registro');
  }

  return await res.json();
}
};