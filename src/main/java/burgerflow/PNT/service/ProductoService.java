package burgerflow.PNT.service;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

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
}