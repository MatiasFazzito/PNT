package burgerflow.PNT.controller;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.DataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/productos")
public class AdminController {

    private final DataService dataService;

    public AdminController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", dataService.getProductos());
        return "admin-productos";
    }

    @GetMapping("/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin-producto-form";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        dataService.guardarProducto(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = dataService.getProductoPorId(id);
        model.addAttribute("producto", producto);
        return "admin-producto-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        dataService.eliminarProducto(id);
        return "redirect:/admin/productos";
    }
}