package burgerflow.PNT.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.DataService;

@Controller
public class MenuController {

    private final DataService dataService;

    public MenuController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/menu")
    public String verMenu(Model model) {
        model.addAttribute("productos", dataService.getProductos());
        return "menu";
    }

    // LISTAR
    @GetMapping("/api/productos")
    @ResponseBody
    public List<Producto> listarProductosApi() {
        return dataService.getProductos();
    }

    // CREAR
    @PostMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<Producto> crearProductoApi(@RequestBody Producto producto) {
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // ACTUALIZAR COMPLETO
    @PutMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<Producto> actualizarProductoApi(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }

    // ACTUALIZAR STOCK
    @PatchMapping("/api/productos/{id}/stock")
    @ResponseBody
    public ResponseEntity<Void> actualizarStockApi(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        dataService.actualizarStock(id, nuevoStock); // Revisa que tu DataService tenga este método
        return ResponseEntity.ok().build();
    }

    // CAMBIAR ESTADO (ACTIVAR / BAJA)
    @PatchMapping("/api/productos/{id}/estado")
    @ResponseBody
    public ResponseEntity<Void> cambiarEstadoApi(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean nuevoEstado = body.get("activo");
        dataService.cambiarEstadoProducto(id, nuevoEstado); // Revisa que tu DataService tenga este método
        return ResponseEntity.ok().build();
    }
}