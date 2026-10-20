package burgerflow.PNT.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.DataService;

// Usar @RestController es la forma moderna de hacer APIs. Nos ahorra poner @ResponseBody en cada método.
// @RequestMapping agrupa la ruta repetida para que no salte el warning ("huerfano" de ruta padre).
@RestController
@RequestMapping("/api/productos")
public class MenuController {

    private final DataService dataService;

    // Inyección de dependencias para comunicarnos con la base de datos a través del DataService.
    public MenuController(DataService dataService) {
        this.dataService = dataService;
    }

    // LISTAR: Como ya estamos en la ruta padre, un @GetMapping vacío hereda "/api/productos".
    // Trae todo el listado para mostrar el menú.
    @GetMapping
    public List<Producto> listarProductosApi() {
        return dataService.getProductos();
    }

    // CREAR: El POST vacío también hereda la ruta. Sirve para agregar una nueva hamburguesa al menú.
    @PostMapping
    public ResponseEntity<Producto> crearProductoApi(@RequestBody Producto producto) {
        // Si al crear el producto no nos avisan si está activo, por defecto lo habilitamos para la venta.
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // ACTUALIZAR COMPLETO: Pisa todos los datos de un producto existente. 
    // La ruta final que se arma es /api/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProductoApi(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // ACTUALIZAR STOCK: Endpoint específico (Patch) para modificar solo la cantidad disponible en el inventario.
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStockApi(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        dataService.actualizarStock(id, nuevoStock); 
        return ResponseEntity.ok().build();
    }

    // CAMBIAR ESTADO (ACTIVAR / BAJA): Sirve para ocultar un producto temporalmente sin borrarlo de la base de datos.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoApi(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("activo");
        dataService.cambiarEstadoProducto(id, nuevoEstado); 
        return ResponseEntity.ok().build();
    }
}