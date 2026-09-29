let PRODUCTOS = [];
let categoriaActiva = 'TODAS';

async function cargarCatalogo() {
  const grid = document.getElementById('grid-productos');
  try {
    PRODUCTOS = await Api.listarProductos();
    pintarFiltros();
    pintarGrid();
  } catch (err) {
    grid.innerHTML = `<p class="vacio">No se pudo cargar el menú. ¿Está corriendo el backend en ${API_BASE}? (${escapeHtml(err.message)})</p>`;
  }
}

function pintarFiltros() {
  const cont = document.getElementById('filtros');
  const categorias = ['TODAS', ...new Set(PRODUCTOS.map(p => p.categoria))];
  cont.innerHTML = categorias.map(cat =>
    `<button class="filtro-chip ${cat === categoriaActiva ? 'activo' : ''}" data-cat="${escapeHtml(cat)}">${escapeHtml(formatearCategoria(cat))}</button>`
  ).join('');

  cont.querySelectorAll('.filtro-chip').forEach(btn => {
    btn.addEventListener('click', () => {
      categoriaActiva = btn.dataset.cat;
      pintarFiltros();
      pintarGrid();
    });
  });
}

function formatearCategoria(cat) {
  if (cat === 'TODAS') return 'Todas';
  return cat.charAt(0) + cat.slice(1).toLowerCase().replace('_', ' ');
}

function pintarGrid() {
  const grid = document.getElementById('grid-productos');
  const lista = categoriaActiva === 'TODAS'
    ? PRODUCTOS
    : PRODUCTOS.filter(p => p.categoria === categoriaActiva);

  if (lista.length === 0) {
    grid.innerHTML = `<p class="vacio">No hay productos en esta categoría todavía.</p>`;
    return;
  }

  grid.innerHTML = lista.map(p => `
    <article class="card-producto ${p.stock === 0 ? 'sin-stock' : ''}">
      <span class="categoria">${escapeHtml(formatearCategoria(p.categoria))}</span>
      <h3>${escapeHtml(p.nombre)}</h3>
      <p>${escapeHtml(p.descripcion)}</p>
      <span class="stock-info ${p.stock === 0 ? 'agotado' : ''}">
        ${p.stock === 0 ? 'Sin stock por ahora' : `Disponible: ${p.stock} u.`}
      </span>
      <div class="fila-precio">
        <span class="precio">$${Number(p.precio).toLocaleString('es-AR')}</span>
        <button class="btn btn-secondary btn-sm" ${p.stock === 0 ? 'disabled' : ''} data-id="${p.id}">Agregar</button>
      </div>
    </article>
  `).join('');

  grid.querySelectorAll('button[data-id]').forEach(btn => {
    btn.addEventListener('click', () => {
      const producto = PRODUCTOS.find(p => p.id === Number(btn.dataset.id));
      Carrito.agregar(producto);
      btn.textContent = 'Agregado ✓';
      setTimeout(() => { btn.textContent = 'Agregar'; }, 900);
    });
  });
}

cargarCatalogo();
