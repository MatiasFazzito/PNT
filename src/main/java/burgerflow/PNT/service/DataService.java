package burgerflow.PNT.service;

import org.springframework.stereotype.Service;

import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.model.Producto;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {
    private List<Producto> productos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();

    public DataService() {
        productos.add(new Producto(1L, "BurgerFlow Doble Bacon", "Doble carne, cheddar y bacon crocante", 8500.0, 10, "Hamburguesas"));
        productos.add(new Producto(2L, "Papas Cheeseburger", "Papas fritas con cheddar fundido y carne", 4200.0, 15, "Acompañamientos"));
        productos.add(new Producto(3L, "Gaseosa 500ml", "Línea Coca-Cola bien fría", 1800.0, 20, "Bebidas"));

        pedidos.add(new Pedido(101L, "cliente@ort.edu.ar", "Pendiente", 12700.0, "Delivery"));
        pedidos.add(new Pedido(100L, "juan@gmail.com", "En preparación", 6000.0, "Retiro"));
    }

    public List<Producto> getProductos() { return productos; }
    public List<Pedido> getPedidos() { return pedidos; }
    
    public void agregarProducto(Producto producto) {
        producto.setId((long) (productos.size() + 1));
        productos.add(producto);
    }

    public void cambiarEstadoPedido(Long id, String nuevoEstado) {
        for (Pedido p : pedidos) {
            if (p.getId().equals(id)) {
                p.setEstado(nuevoEstado);
                break;
            }
        }
    }
}