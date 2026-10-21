package burgerflow.PNT.controller;

import burgerflow.PNT.model.EstadoPedido;
import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.DataService;
import burgerflow.PNT.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cocina")
public class KitchenController {

    private final DataService dataService;
    private final PedidoService pedidoService;

    public KitchenController(DataService dataService, PedidoService pedidoService) {
        this.dataService = dataService;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String verCocina(HttpSession session, Model model) {

        Object objUsuario = session.getAttribute("usuario");
        if (objUsuario == null) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) objUsuario;

        String rol = usuario.getRol();
        if (!"ADMIN".equalsIgnoreCase(rol) && !"EMPLEADO".equalsIgnoreCase(rol)) {
            return "redirect:/";
        }

        model.addAttribute("pedidos", dataService.getPedidos());
        return "cocina";
    }

    @PostMapping("/pedido/estado")
    public String cambiarEstadoPedido(@RequestParam Long id, @RequestParam String estado, HttpSession session) {
        Object objUsuario = session.getAttribute("usuario");

        if (objUsuario == null) {
            return "redirect:/login";
        }

        Usuario usuario = (Usuario) objUsuario;
        String rol = usuario.getRol();

        if (!"ADMIN".equalsIgnoreCase(rol) && !"EMPLEADO".equalsIgnoreCase(rol)) {
            return "redirect:/";
        }

        try {
            pedidoService.cambiarEstado(id, EstadoPedido.desdeTexto(estado));
        } catch (RuntimeException e) {
            System.out.println("No se pudo cambiar el estado del pedido " + id + ": " + e.getMessage());
        }
        return "redirect:/cocina";
    }
}
