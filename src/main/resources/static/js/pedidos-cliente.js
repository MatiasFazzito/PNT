requiereRol('CLIENTE');

async function cargarMisPedidos() {
  const cont = document.getElementById('lista-pedidos');
  try {
    const pedidos = await Api.misPedidos();
    if (pedidos.length === 0) {
      cont.innerHTML = `<p class="vacio">Todavía no hiciste ningún pedido. <a href="index.html">Andá al menú</a>.</p>`;
      return;
    }
    cont.innerHTML = pedidos.map(p => `
      <div class="ticket-pedido estado-${p.estado}">
        <div class="ticket-cabecera">
          <h3>Pedido #${p.id} · ${p.tipoEntrega === 'DELIVERY' ? 'Delivery' : 'Retiro en local'}</h3>
          <span class="badge-estado">${etiquetaEstado(p.estado)}</span>
        </div>
        <ul class="ticket-items">${renderItemsPedido(p.items)}</ul>
        ${p.direccionEntrega ? `<p style="font-size:0.85rem;color:var(--ink-soft)">Entrega en: ${escapeHtml(p.direccionEntrega)}</p>` : ''}
        ${p.observaciones ? `<p style="font-size:0.85rem;color:var(--ink-soft)">Obs: ${escapeHtml(p.observaciones)}</p>` : ''}
        <p style="font-weight:700">Total: $${Number(p.total).toLocaleString('es-AR')}</p>
        <p style="font-size:0.78rem;color:var(--ink-soft)">Realizado el ${formatearFecha(p.fechaCreacion)}</p>
      </div>
    `).join('');
  } catch (err) {
    cont.innerHTML = `<p class="vacio">No se pudieron cargar tus pedidos (${escapeHtml(err.message)})</p>`;
  }
}

document.getElementById('btn-refrescar').addEventListener('click', cargarMisPedidos);
cargarMisPedidos();
