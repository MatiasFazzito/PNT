package burgerflow.PNT.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import burgerflow.PNT.model.Producto;
import burgerflow.PNT.service.DataService;

@Controller
public class AdminController {

    @Autowired
    private DataService dataService;

    @GetMapping("/admin/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", dataService.getProductos());
        return "admin-productos";
    }

    @GetMapping("/admin/productos/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin-producto-form";
    }

    @PostMapping("/admin/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        dataService.agregarProducto(producto);
        return "redirect:/admin/productos";
    }
}
