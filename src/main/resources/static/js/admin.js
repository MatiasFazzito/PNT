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

// Categorías inicializadas únicamente con las tres requeridas. Se pueden agregar más dinámicamente.
let CATEGORIAS = ['Hamburguesa', 'Acompañamiento', 'Bebida'];
const NUEVA_CATEGORIA = '__nueva__';

// Unificar nombres para que cuenten como la misma categoría (evita duplicados por mayúsculas/minúsculas o tildes)
function normalizarCategoria(texto) {
  return (texto || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toLowerCase();
}

function poblarSelectCategorias(seleccionada) {
  const select = document.getElementById('p-categoria');
  select.innerHTML = '';
  select.add(new Option('Seleccioná una categoría', ''));
  CATEGORIAS.forEach(c => select.add(new Option(c, c)));
  select.add(new Option('+ Agregar Categoria', NUEVA_CATEGORIA));

  if (seleccionada) {
    const existente = CATEGORIAS.find(c => normalizarCategoria(c) === normalizarCategoria(seleccionada));
    if (existente) {
      select.value = existente;
    } else {
      CATEGORIAS.push(seleccionada);
      CATEGORIAS.sort((a, b) => a.localeCompare(b, 'es'));
      poblarSelectCategorias(seleccionada);
      return;
    }
  }
  actualizarCampoCategoriaNueva();
}

// Muestra el campo de texto solo cuando se elige "Nueva categoria"
function actualizarCampoCategoriaNueva() {
  const esNueva = document.getElementById('p-categoria').value === NUEVA_CATEGORIA;
  const input = document.getElementById('p-categoria-nueva');
  input.classList.toggle('oculto', !esNueva);
  input.required = esNueva;
}

// Devuelve la categoría elegida. Si escribieron una nueva, valida que no exista (evita duplicados) y la agrega.
function categoriaElegida() {
  const select = document.getElementById('p-categoria');
  if (select.value !== NUEVA_CATEGORIA) return select.value;

  const escrita = document.getElementById('p-categoria-nueva').value.trim();
  if (!escrita) return '';

  // Verificar si ya existe en la lista (ignorando mayúsculas y acentos) para evitar duplicados
  const existente = CATEGORIAS.find(c => normalizarCategoria(c) === normalizarCategoria(escrita));
  if (existente) return existente;

  // Si es totalmente nueva, la agregamos a la lista en memoria y la ordenamos alfabéticamente
  CATEGORIAS.push(escrita);
  CATEGORIAS.sort((a, b) => a.localeCompare(b, 'es'));
  return escrita;
}

document.getElementById('p-categoria').addEventListener('change', actualizarCampoCategoriaNueva);

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
  document.getElementById('p-categoria-nueva').value = '';
  poblarSelectCategorias(producto ? producto.categoria : '');
  document.getElementById('p-precio').value = producto ? producto.precio : '';
  document.getElementById('p-stock').value = producto ? producto.stock : '';
  document.getElementById('p-imagen').value = producto ? (producto.imagen || '') : '';
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
    categoria: categoriaElegida(),
    precio: Number(document.getElementById('p-precio').value),
    stock: Number(document.getElementById('p-stock').value),
    imagen: document.getElementById('p-imagen').value.trim()
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
      const cliente = p.clienteNombre
        ? (p.clienteEmail ? `${p.clienteNombre} (${p.clienteEmail})` : p.clienteNombre)
        : (p.clienteEmail || p.cliente || 'Sin cliente');
      const tipo = p.tipo || p.tipoEntrega || 'Delivery';
      const total = Number(p.total || 0).toLocaleString('es-AR');
      const estado = p.estado || 'RECIBIDO';
      const siguientes = TRANSICIONES_ESTADO[estado] || [];

      return `
        <div class="pedido-card" data-id="${id}">
          <div class="pedido-header">
            <strong>Pedido #${id}</strong>
            <span class="badge">${etiquetaEstado(estado)}</span>
          </div>
          <div class="pedido-info">
            <p><strong>Cliente:</strong> ${escapeHtml(cliente)}</p>
            <p><strong>Tipo:</strong> ${escapeHtml(etiquetaEntrega(tipo))}</p>
            ${p.cargadoPor ? `<p><strong>Cargado por:</strong> ${escapeHtml(p.cargadoPor)}</p>` : ''}
            <p><strong>Total:</strong> $${total}</p>
          </div>
          <div class="pedido-acciones">
            ${siguientes.length > 0 ? `
              <select class="select-estado" data-id="${id}">
                ${siguientes.map(e => `<option value="${e}">${etiquetaEstado(e)}</option>`).join('')}
              </select>
              <button class="btn btn-sm btn-outline btn-cambiar-estado" data-id="${id}">Cambiar estado</button>
            ` : '<em>Pedido finalizado</em>'}
          </div>
        </div>
      `;
    }).join('');

    contenedor.querySelectorAll('.btn-cambiar-estado').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.id;
        const select = contenedor.querySelector(`.select-estado[data-id="${id}"]`);
        if (!select) return;

        btn.disabled = true;
        try {
          await Api.cambiarEstadoPedido(id, select.value);
          cargarPedidos();
        } catch (err) {
          alert('Error al actualizar el estado: ' + err.message);
          btn.disabled = false;
        }
      });
    });

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
