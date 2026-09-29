package burgerflow.PNT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import burgerflow.PNT.service.DataService;

@Controller
public class KitchenController {

    @Autowired
    private DataService dataService;

    @GetMapping("/cocina")
    public String panelCocina(Model model) {
        model.addAttribute("pedidos", dataService.getPedidos());
        return "cocina";
    }

    @PostMapping("/cocina/cambiar-estado")
    public String cambiarEstado(@RequestParam Long pedidoId, @RequestParam String nuevoEstado) {
        dataService.cambiarEstadoPedido(pedidoId, nuevoEstado);
        return "redirect:/cocina";
    }
}