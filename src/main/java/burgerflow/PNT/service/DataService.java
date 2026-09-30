package burgerflow.PNT.service;

import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.model.Producto;
import burgerflow.PNT.repository.PedidoRepository;
import burgerflow.PNT.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    public DataService(ProductoRepository productoRepository, PedidoRepository pedidoRepository) {
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    public List<Pedido> getPedidos() {
        return pedidoRepository.findAll();
    }

    public void agregarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    public void guardarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    public Producto getProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public void cambiarEstadoPedido(Long id, String nuevoEstado) {
        pedidoRepository.findById(id).ifPresent(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepository.save(p);
        });
    }
}