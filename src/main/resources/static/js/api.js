// Cliente HTTP central. Cambiá API_BASE si el backend corre en otro host/puerto.
const API_BASE = 'http://localhost:8080/api';

// Escapa texto antes de insertarlo con innerHTML (evita inyección de HTML/JS desde nombres, observaciones, etc.).
function escapeHtml(valor) {
  return String(valor ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

const Api = {
  token() {
    return localStorage.getItem('bf_token');
  },

  usuario() {
    const raw = localStorage.getItem('bf_usuario');
    return raw ? JSON.parse(raw) : null;
  },

  guardarSesion(authResponse) {
    localStorage.setItem('bf_token', authResponse.token);
    localStorage.setItem('bf_usuario', JSON.stringify({
      id: authResponse.id,
      nombre: authResponse.nombre,
      email: authResponse.email,
      rol: authResponse.rol
    }));
  },

  cerrarSesion() {
    localStorage.removeItem('bf_token');
    localStorage.removeItem('bf_usuario');
  },

  estaLogueado() {
    return !!this.token();
  },

  tieneRol(...roles) {
    const u = this.usuario();
    return !!u && roles.includes(u.rol);
  },

  async request(path, { method = 'GET', body = null, auth = false } = {}) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth && this.token()) {
      headers['Authorization'] = `Bearer ${this.token()}`;
    }

    const res = await fetch(`${API_BASE}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : null
    });

    if (res.status === 204) return null;

    // Token vencido o inválido en un endpoint protegido: se cierra la sesión y se vuelve al login.
    if (res.status === 401 && auth) {
      this.cerrarSesion();
      window.location.href = 'login.html';
      throw new Error('Tu sesión expiró. Ingresá nuevamente.');
    }

    let data = null;
    try { data = await res.json(); } catch (e) { /* respuesta vacía */ }

    if (!res.ok) {
      const mensaje = data?.mensaje || data?.detalles?.join(' | ') || `Error ${res.status}`;
      throw new Error(mensaje);
    }
    return data;
  },

  // --- Auth ---
  login(email, password) {
    return this.request('/auth/login', { method: 'POST', body: { email, password } });
  },
  registrar(datos) {
    return this.request('/auth/registro', { method: 'POST', body: datos });
  },
  perfil() {
    return this.request('/auth/perfil', { auth: true });
  },

  // --- Productos ---
  listarProductos() {
    return this.request('/productos');
  },
  listarProductosTodos() {
    return this.request('/productos/todos', { auth: true });
  },
  crearProducto(datos) {
    return this.request('/productos', { method: 'POST', body: datos, auth: true });
  },
  actualizarProducto(id, datos) {
    return this.request(`/productos/${id}`, { method: 'PUT', body: datos, auth: true });
  },
  actualizarStock(id, stock) {
    return this.request(`/productos/${id}/stock`, { method: 'PATCH', body: { stock }, auth: true });
  },
  eliminarProducto(id) {
    return this.request(`/productos/${id}`, { method: 'DELETE', auth: true });
  },

  // --- Pedidos ---
  crearPedido(datos) {
    return this.request('/pedidos', { method: 'POST', body: datos, auth: true });
  },
  misPedidos() {
    return this.request('/pedidos/mis-pedidos', { auth: true });
  },
  pedidosCocina() {
    return this.request('/pedidos/cocina', { auth: true });
  },
  todosLosPedidos() {
    return this.request('/pedidos', { auth: true });
  },
  cambiarEstadoPedido(id, estado) {
    return this.request(`/pedidos/${id}/estado`, { method: 'PATCH', body: { estado }, auth: true });
  },

  // --- Usuarios (admin) ---
  listarUsuarios() {
    return this.request('/usuarios', { auth: true });
  },
  actualizarUsuario(id, datos) {
    return this.request(`/usuarios/${id}`, { method: 'PATCH', body: datos, auth: true });
  },
  darDeBajaUsuario(id) {
    return this.request(`/usuarios/${id}`, { method: 'DELETE', auth: true });
  }
};
