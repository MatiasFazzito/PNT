// Carrito de compras en localStorage
const Carrito = {
  CLAVE: 'bf_carrito',

  obtener() {
    const raw = localStorage.getItem(this.CLAVE);
    return raw ? JSON.parse(raw) : [];
  },

  guardar(items) {
    localStorage.setItem(this.CLAVE, JSON.stringify(items));
  },

  agregar(producto) {
    const items = this.obtener();
    const existente = items.find(i => i.productoId === producto.id);
    if (existente) {
      if (existente.cantidad < producto.stock) existente.cantidad += 1;
    } else {
      items.push({
        productoId: producto.id,
        nombre: producto.nombre,
        precio: Number(producto.precio),
        stockDisponible: producto.stock,
        cantidad: 1
      });
    }
    this.guardar(items);
  },

  actualizarCantidad(productoId, cantidad) {
    let items = this.obtener();
    items = items.map(i => i.productoId === productoId ? { ...i, cantidad } : i);
    items = items.filter(i => i.cantidad > 0);
    this.guardar(items);
  },

  quitar(productoId) {
    const items = this.obtener().filter(i => i.productoId !== productoId);
    this.guardar(items);
  },

  vaciar() {
    localStorage.removeItem(this.CLAVE);
  },

  total() {
    return this.obtener().reduce((acc, i) => acc + (i.precio * i.cantidad), 0);
  }
};

function pintarCarrito() {
  const items = Carrito.obtener();
  const vacio = document.getElementById('carrito-vacio');
  const contenido = document.getElementById('carrito-contenido');

  if (items.length === 0) {
    vacio.classList.remove('oculto');
    contenido.classList.add('oculto');
    return;
  }
  vacio.classList.add('oculto');
  contenido.classList.remove('oculto');

  document.getElementById('filas-carrito').innerHTML = items.map(i => `
    <tr>
      <td>${escapeHtml(i.nombre)}</td>
      <td>$${i.precio.toLocaleString('es-AR')}</td>
      <td>
        <div class="cant-control">
          <button data-accion="menos" data-id="${i.productoId}">−</button>
          <span>${i.cantidad}</span>
          <button data-accion="mas" data-id="${i.productoId}">+</button>
        </div>
      </td>
      <td>$${(i.precio * i.cantidad).toLocaleString('es-AR')}</td>
      <td><button class="btn-link" data-accion="quitar" data-id="${i.productoId}" style="color:var(--tomato)">Quitar</button></td>
    </tr>
  `).join('');

  document.getElementById('total-carrito').textContent = '$' + Carrito.total().toLocaleString('es-AR');

  document.querySelectorAll('[data-accion]').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = Number(btn.dataset.id);
      const item = Carrito.obtener().find(i => i.productoId === id);
      if (btn.dataset.accion === 'mas' && item.cantidad < item.stockDisponible) {
        Carrito.actualizarCantidad(id, item.cantidad + 1);
      } else if (btn.dataset.accion === 'menos') {
        Carrito.actualizarCantidad(id, item.cantidad - 1);
      } else if (btn.dataset.accion === 'quitar') {
        Carrito.quitar(id);
      }
      pintarCarrito();
    });
  });
}

// Personal (ADMIN/EMPLEADO): carga el pedido a nombre de un cliente (mostrador/teléfono)
const esPersonal = Api.tieneRol('ADMIN', 'EMPLEADO');
if (esPersonal) {
  document.getElementById('campo-cliente').style.display = 'block';
  const selectEntrega = document.getElementById('tipoEntrega');
  selectEntrega.insertBefore(new Option('Consumo en mostrador', 'MOSTRADOR'), selectEntrega.firstChild);
  selectEntrega.value = 'MOSTRADOR';
  document.getElementById('btn-confirmar').textContent = 'Cargar pedido';
}

document.getElementById('tipoEntrega').addEventListener('change', (e) => {
  document.getElementById('campo-direccion').style.display = e.target.value === 'DELIVERY' ? 'block' : 'none';
});

document.getElementById('form-checkout').addEventListener('submit', async (e) => {
  e.preventDefault();
  const mensaje = document.getElementById('mensaje');
  mensaje.className = 'form-mensaje';

  if (!Api.estaLogueado()) {
    window.location.href = 'login.html';
    return;
  }

  const items = Carrito.obtener();
  if (items.length === 0) return;

  const datos = {
    tipoEntrega: document.getElementById('tipoEntrega').value,
    direccionEntrega: document.getElementById('direccion').value.trim(),
    observaciones: document.getElementById('observaciones').value.trim(),
    items: items.map(i => ({ productoId: i.productoId, cantidad: i.cantidad }))
  };

  if (esPersonal) {
    const nombre = document.getElementById('clienteNombre').value.trim();
    if (!nombre) {
      mensaje.textContent = 'Ingresá el nombre del cliente';
      mensaje.className = 'form-mensaje error';
      return;
    }
    datos.clienteNombre = nombre;
  }

  try {
    await Api.crearPedido(datos);
    Carrito.vaciar();
    mensaje.textContent = esPersonal
      ? '¡Pedido cargado! Lo ves en la cola de cocina.'
      : '¡Pedido confirmado! Podés seguir su estado en "Mis pedidos".';
    mensaje.className = 'form-mensaje ok';
    setTimeout(() => { window.location.href = esPersonal ? '/cocina' : 'mis-pedidos.html'; }, 1200);
  } catch (err) {
    mensaje.textContent = err.message;
    mensaje.className = 'form-mensaje error';
  }
});

pintarCarrito();
