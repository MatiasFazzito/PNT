package burgerflow.PNT.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class MenuController {

    private final ProductoService productoService;

    public MenuController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET: Trae todo el listado para mostrar el menú.
    @GetMapping
    public List<Producto> listarProductosApi() {
        return productoService.listarTodos();
    }

    // POST: Sirve para agregar un item al menú.
    @PostMapping
    public ResponseEntity<Producto> crearProductoApi(@RequestBody Producto producto) {
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.ok(guardado);
    }

    // PUT: Pisa todos los datos de un producto existente.
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProductoApi(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        Producto actualizado = productoService.guardar(producto);
        return ResponseEntity.ok(actualizado);
    }

    // PATCH: Endpoint específico para modificar solo la cantidad disponible en el
    // inventario.
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStockApi(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");

        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }
        producto.setStock(nuevoStock);
        productoService.guardar(producto);

        return ResponseEntity.ok().build();
    }

    // PATCH: Sirve para ocultar un producto temporalmente sin borrarlo de la base
    // de datos.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoApi(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("activo");

        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }
        producto.setActivo(nuevoEstado);
        productoService.guardar(producto);

        return ResponseEntity.ok().build();
    }
}