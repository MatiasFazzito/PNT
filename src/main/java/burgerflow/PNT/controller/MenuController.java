package burgerflow.PNT.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.DataService;

// Este controlador maneja las rutas relacionadas con el menú de productos a través de la API REST.

@RestController
@RequestMapping("/api/productos")
public class MenuController {

    private final DataService dataService;

    public MenuController(DataService dataService) {
        this.dataService = dataService;
    }

    // GET: Trae todo el listado para mostrar el menú.
    @GetMapping
    public List<Producto> listarProductosApi() {
        return dataService.getProductos();
    }

    // POST: Sirve para agregar un item al menú.
    @PostMapping
    public ResponseEntity<Producto> crearProductoApi(@RequestBody Producto producto) {
        // Si al crear el producto no nos avisan si está activo, por defecto lo habilitamos para la venta.
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // PUT: Pisa todos los datos de un producto existente. 
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProductoApi(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // PATCH: Endpoint específico para modificar solo la cantidad disponible en el inventario.
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStockApi(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        dataService.actualizarStock(id, nuevoStock); 
        return ResponseEntity.ok().build();
    }

    // PATCH: Sirve para ocultar un producto temporalmente sin borrarlo de la base de datos.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoApi(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("activo");
        dataService.cambiarEstadoProducto(id, nuevoEstado); 
        return ResponseEntity.ok().build();
    }
}