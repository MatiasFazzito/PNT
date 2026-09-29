package burgerflow.PNT.controller;

import burgerflow.PNT.service.DataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cocina")
public class KitchenController {

    private final DataService dataService;

    public KitchenController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public String verCocina(Model model) {
        model.addAttribute("pedidos", dataService.getPedidos());
        return "cocina";
    }

    @PostMapping("/pedido/estado")
    public String cambiarEstadoPedido(@RequestParam Long id, @RequestParam String estado) {
        dataService.cambiarEstadoPedido(id, estado);
        return "redirect:/cocina";
    }
}