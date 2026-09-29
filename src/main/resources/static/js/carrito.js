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
