requiereRol('ADMIN');

// --- Tabs ---
document.querySelectorAll('.tab-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('activo'));
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('activo'));
    btn.classList.add('activo');
    document.getElementById('tab-' + btn.dataset.tab).classList.add('activo');
  });
});

// ================= PRODUCTOS =================
async function cargarProductos() {
  const tbody = document.getElementById('filas-productos');
  try {
    const productos = await Api.listarProductos();
    tbody.innerHTML = productos.map(p => `
      <tr class="${p.activo ? '' : 'inactivo'}">
        <td>${escapeHtml(p.nombre)}</td>
        <td>${escapeHtml(p.categoria)}</td>
        <td>$${Number(p.precio).toLocaleString('es-AR')}</td>
        <td>
          <input type="number" min="0" value="${p.stock}" data-stock-id="${p.id}" style="width:70px;padding:4px">
        </td>
        <td>${p.activo ? 'Activo' : 'Dado de baja'}</td>
        <td style="display:flex;gap:6px">
          <button class="btn btn-outline btn-sm" data-editar="${p.id}">Editar</button>
          <button class="btn ${p.activo ? 'btn-danger' : 'btn-success'} btn-sm" 
                  data-toggle-estado="${p.id}" 
                  data-activo="${p.activo}">
            ${p.activo ? 'Dar de baja' : 'Activar'}
          </button>
        </td>
      </tr>
    `).join('');

    tbody.querySelectorAll('[data-stock-id]').forEach(input => {
      input.addEventListener('change', async () => {
        try {
          await Api.actualizarStock(input.dataset.stockId, Number(input.value));
        } catch (err) {
          alert(err.message);
          cargarProductos();
        }
      });
    });

    tbody.querySelectorAll('[data-editar]').forEach(btn => {
      btn.addEventListener('click', () => {
        const p = productos.find(x => x.id === Number(btn.dataset.editar));
        abrirModalProducto(p);
      });
    });

    tbody.querySelectorAll('[data-toggle-estado]').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.toggleEstado;
        const estaActivo = btn.dataset.activo === 'true';
        const mensaje = estaActivo ? '¿Dar de baja este producto?' : '¿Activar este producto?';

        if (!confirm(mensaje)) return;

        try {
          await Api.cambiarEstadoProducto(id, !estaActivo);
          cargarProductos();
        } catch (err) {
          alert(err.message);
        }
      });
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6">Error al cargar productos: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function abrirModalProducto(producto) {
  document.getElementById('modal-mensaje').className = 'form-mensaje';
  document.getElementById('modal-titulo').textContent = producto ? 'Editar producto' : 'Nuevo producto';
  document.getElementById('p-id').value = producto ? producto.id : '';
  document.getElementById('p-nombre').value = producto ? producto.nombre : '';
  document.getElementById('p-descripcion').value = producto ? (producto.descripcion || '') : '';
  document.getElementById('p-categoria').value = producto ? producto.categoria : '';
  document.getElementById('p-precio').value = producto ? producto.precio : '';
  document.getElementById('p-stock').value = producto ? producto.stock : '';
  document.getElementById('p-imagen').value = producto ? (producto.imagenUrl || '') : '';
  document.getElementById('modal-producto').classList.remove('oculto');
}

document.getElementById('btn-nuevo-producto').addEventListener('click', () => abrirModalProducto(null));
document.getElementById('btn-cerrar-modal').addEventListener('click', () => {
  document.getElementById('modal-producto').classList.add('oculto');
});

document.getElementById('form-producto').addEventListener('submit', async (e) => {
  e.preventDefault();
  const mensaje = document.getElementById('modal-mensaje');
  mensaje.className = 'form-mensaje';

  const id = document.getElementById('p-id').value;
  const datos = {
    nombre: document.getElementById('p-nombre').value.trim(),
    descripcion: document.getElementById('p-descripcion').value.trim(),
    categoria: document.getElementById('p-categoria').value.trim(),
    precio: Number(document.getElementById('p-precio').value),
    stock: Number(document.getElementById('p-stock').value),
    imagenUrl: document.getElementById('p-imagen').value.trim()
  };

  try {
    if (id) {
      await Api.actualizarProducto(id, datos);
    } else {
      await Api.crearProducto(datos);
    }
    document.getElementById('modal-producto').classList.add('oculto');
    cargarProductos();
  } catch (err) {
    mensaje.textContent = err.message;
    mensaje.className = 'form-mensaje error';
  }
});

async function cargarPedidos() {
  const contenedor = document.getElementById('lista-pedidos-admin');
  if (!contenedor) return;

  try {
    const respuesta = await Api.todosLosPedidos();
    const pedidos = Array.isArray(respuesta) ? respuesta : [];

    if (pedidos.length === 0) {
      contenedor.innerHTML = '<p class="vacio">No hay pedidos registrados.</p>';
      return;
    }

    contenedor.innerHTML = pedidos.map(p => {
      const id = p.id ?? '-';
      const cliente = p.clienteEmail || p.cliente || 'Sin cliente';
      const tipo = p.tipo || p.tipoEntrega || 'Delivery';
      const total = Number(p.total || 0).toLocaleString('es-AR');
      const estado = p.estado || 'Pendiente';

      return `
        <div class="pedido-card" data-id="${id}">
          <div class="pedido-header">
            <strong>Pedido #${id}</strong>
            <span class="badge ${estado.toLowerCase()}">${estado}</span>
          </div>
          <div class="pedido-info">
            <p><strong>Cliente:</strong> ${cliente}</p>
            <p><strong>Tipo:</strong> ${tipo}</p>
            <p><strong>Total:</strong> $${total}</p>
          </div>
          <div class="pedido-acciones">
            <button class="btn btn-sm btn-outline" data-pedido-id="${id}">Cambiar Estado</button>
          </div>
        </div>
      `;
    }).join('');

  } catch (err) {
    console.error('Error al cargar pedidos:', err);
    contenedor.innerHTML = `<p class="error">Error al cargar pedidos: ${err.message}</p>`;
  }
}

// ================= USUARIOS =================
async function cargarUsuarios() {
  const tbody = document.getElementById('filas-usuarios');
  try {
    const usuarios = await Api.listarUsuarios();
    tbody.innerHTML = usuarios.map(u => `
      <tr class="${u.activo ? '' : 'inactivo'}">
        <td>${escapeHtml(u.nombre)}</td>
        <td>${escapeHtml(u.email)}</td>
        <td>
          <select data-rol-id="${u.id}">
            <option value="CLIENTE" ${u.rol === 'CLIENTE' ? 'selected' : ''}>Cliente</option>
            <option value="EMPLEADO" ${u.rol === 'EMPLEADO' ? 'selected' : ''}>Empleado</option>
            <option value="ADMIN" ${u.rol === 'ADMIN' ? 'selected' : ''}>Admin</option>
          </select>
        </td>
        <td>${u.activo ? 'Activo' : 'Dado de baja'}</td>
        <td>${u.activo ? `<button class="btn btn-danger btn-sm" data-baja-usuario="${u.id}">Dar de baja</button>` : ''}</td>
      </tr>
    `).join('');

    tbody.querySelectorAll('[data-rol-id]').forEach(select => {
      select.addEventListener('change', async () => {
        try {
          await Api.actualizarUsuario(select.dataset.rolId, { rol: select.value });
        } catch (err) {
          alert(err.message);
          cargarUsuarios();
        }
      });
    });

    tbody.querySelectorAll('[data-baja-usuario]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('¿Dar de baja este usuario?')) return;
        try {
          await Api.darDeBajaUsuario(btn.dataset.bajaUsuario);
          cargarUsuarios();
        } catch (err) {
          alert(err.message);
        }
      });
    });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5">Error al cargar usuarios: ${escapeHtml(err.message)}</td></tr>`;
  }
}

cargarProductos();
cargarPedidos();
cargarUsuarios();
