package burgerflow.PNT.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/api/productos")
    @ResponseBody
    public List<Producto> listarProductosApi() {
        return dataService.getProductos();
    }

    @PostMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<Producto> crearProductoApi(@RequestBody Producto producto) {
        dataService.guardarProducto(producto);
        return ResponseEntity.ok(producto);
    }
}