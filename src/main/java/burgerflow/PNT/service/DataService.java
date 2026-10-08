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

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto getProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // --- MÉTODOS PARA ACTUALIZACIÓN PARCIAL (PATCH) ---

    public void actualizarStock(Long id, Integer nuevoStock) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setStock(nuevoStock);
            productoRepository.save(p);
        });
    }

    public void cambiarEstadoProducto(Long id, Boolean nuevoEstado) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(nuevoEstado);
            productoRepository.save(p);
        });
    }

    public void cambiarEstadoPedido(Long id, String nuevoEstado) {
        pedidoRepository.findById(id).ifPresent(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepository.save(p);
        });
    }
}