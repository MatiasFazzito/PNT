requiereRol('EMPLEADO', 'ADMIN');

const SIGUIENTE_ESTADO = {
  RECIBIDO: { estado: 'EN_PREPARACION', etiqueta: 'Empezar preparación' },
  EN_PREPARACION: { estado: 'LISTO_PARA_ENTREGAR', etiqueta: 'Marcar listo' },
  LISTO_PARA_ENTREGAR: { estado: 'ENTREGADO', etiqueta: 'Marcar entregado' }
};

async function cargarPedidosCocina() {
  const cont = document.getElementById('lista-pedidos');
  try {
    const pedidos = await Api.pedidosCocina();
    if (pedidos.length === 0) {
      cont.innerHTML = `<p class="vacio">No hay pedidos activos en este momento.</p>`;
      return;
    }
    cont.innerHTML = pedidos.map(p => {
      const siguiente = SIGUIENTE_ESTADO[p.estado];
      return `
      <div class="ticket-pedido estado-${p.estado}">
        <div class="ticket-cabecera">
          <h3>Pedido #${p.id} · ${escapeHtml(p.clienteNombre)}</h3>
          <span class="badge-estado">${etiquetaEstado(p.estado)}</span>
        </div>
        <p style="font-size:0.85rem;color:var(--ink-soft)">${p.tipoEntrega === 'DELIVERY' ? 'Delivery a ' + escapeHtml(p.direccionEntrega || 's/d') : 'Retiro en local'}</p>
        <ul class="ticket-items">${renderItemsPedido(p.items)}</ul>
        ${p.observaciones ? `<p style="font-size:0.85rem;color:var(--ink-soft)">Obs: ${escapeHtml(p.observaciones)}</p>` : ''}
        <p style="font-size:0.78rem;color:var(--ink-soft)">Pedido a las ${formatearFecha(p.fechaCreacion)}</p>
        <div class="ticket-acciones">
          ${siguiente ? `<button class="btn btn-secondary btn-sm" data-id="${p.id}" data-estado="${siguiente.estado}">${siguiente.etiqueta}</button>` : ''}
          <button class="btn btn-danger btn-sm" data-id="${p.id}" data-estado="CANCELADO">Cancelar (sin insumos)</button>
        </div>
      </div>
    `;
    }).join('');

    cont.querySelectorAll('button[data-estado]').forEach(btn => {
      btn.addEventListener('click', async () => {
        btn.disabled = true;
        try {
          await Api.cambiarEstadoPedido(btn.dataset.id, btn.dataset.estado);
          cargarPedidosCocina();
        } catch (err) {
          alert(err.message);
          btn.disabled = false;
        }
      });
    });
  } catch (err) {
    cont.innerHTML = `<p class="vacio">No se pudieron cargar los pedidos (${escapeHtml(err.message)})</p>`;
  }
}

document.getElementById('btn-refrescar').addEventListener('click', cargarPedidosCocina);
cargarPedidosCocina();
