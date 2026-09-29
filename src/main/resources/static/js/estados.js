const ETIQUETAS_ESTADO = {
  RECIBIDO: 'Recibido',
  EN_PREPARACION: 'En preparación',
  LISTO_PARA_ENTREGAR: 'Listo para entregar',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado'
};

function etiquetaEstado(estado) {
  return ETIQUETAS_ESTADO[estado] || estado;
}

function formatearFecha(fechaIso) {
  const f = new Date(fechaIso);
  return f.toLocaleString('es-AR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' });
}

function renderItemsPedido(items) {
  return items.map(i => `<li>${i.cantidad} × ${escapeHtml(i.productoNombre)} — $${Number(i.subtotal).toLocaleString('es-AR')}</li>`).join('');
}
