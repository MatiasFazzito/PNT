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
  guardarSesion(usuario) {
    localStorage.setItem('usuario', JSON.stringify(usuario));
  },

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

  async listarProductos() {
    const response = await fetch('/api/productos');
    if (!response.ok) {
      throw new Error(`Error al obtener productos: ${response.status}`);
    }
    return await response.json();
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
  },

  async crearProducto(producto) {
    const res = await fetch('/api/productos', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(producto)
    });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || 'Error al guardar el producto');
    }

    return await res.json();
  },

  async actualizarProducto(id, producto) {
    const response = await fetch(`/api/productos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(producto)
    });
    if (!response.ok) throw new Error('Error al actualizar producto');
    return await response.json();
  },

  async actualizarStock(id, stock) {
    const res = await fetch(`/api/productos/${id}/stock`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ stock })
    });
    if (!res.ok) throw new Error('Error al actualizar el stock');
  },

  async cambiarEstadoProducto(id, activo) {
    const res = await fetch(`/api/productos/${id}/estado`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ activo })
    });
    if (!res.ok) throw new Error('Error al cambiar estado del producto');
  },

  async todosLosPedidos() {
    const res = await fetch('/api/pedidos');
    if (!res.ok) throw new Error('Error al obtener la lista de pedidos');
    return await res.json();
  },

  // CAMBIAR ESTADO DE UN PEDIDO (Pendiente, En preparación, Entregado, etc.)
  async cambiarEstadoPedido(id, nuevoEstado) {
    const res = await fetch(`/api/pedidos/${id}/estado`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: nuevoEstado })
    });
    if (!res.ok) throw new Error('Error al actualizar el estado del pedido');
  }
};