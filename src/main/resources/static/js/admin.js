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
    const productos = await Api.listarProductosTodos();
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
          ${p.activo ? `<button class="btn btn-danger btn-sm" data-baja="${p.id}">Dar de baja</button>` : ''}
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

    tbody.querySelectorAll('[data-baja]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('¿Dar de baja este producto?')) return;
        try {
          await Api.eliminarProducto(btn.dataset.baja);
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

// ================= PEDIDOS =================
async function cargarPedidosAdmin() {
  const cont = document.getElementById('lista-pedidos-admin');
  try {
    const pedidos = await Api.todosLosPedidos();
    if (pedidos.length === 0) {
      cont.innerHTML = `<p class="vacio">Todavía no hay pedidos registrados.</p>`;
      return;
    }
    cont.innerHTML = pedidos.map(p => `
      <div class="ticket-pedido estado-${p.estado}">
        <div class="ticket-cabecera">
          <h3>Pedido #${p.id} · ${escapeHtml(p.clienteNombre)}</h3>
          <span class="badge-estado">${etiquetaEstado(p.estado)}</span>
        </div>
        <ul class="ticket-items">${renderItemsPedido(p.items)}</ul>
        <p style="font-weight:700">Total: $${Number(p.total).toLocaleString('es-AR')}</p>
        <p style="font-size:0.78rem;color:var(--ink-soft)">${formatearFecha(p.fechaCreacion)} · ${escapeHtml(p.tipoEntrega)}</p>
      </div>
    `).join('');
  } catch (err) {
    cont.innerHTML = `<p class="vacio">No se pudieron cargar los pedidos (${escapeHtml(err.message)})</p>`;
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
cargarPedidosAdmin();
cargarUsuarios();
