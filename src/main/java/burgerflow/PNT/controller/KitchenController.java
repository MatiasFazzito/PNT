package burgerflow.PNT.controller;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.DataService;
import jakarta.servlet.http.HttpSession;
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
    public String verCocina(HttpSession session, Model model) {
        System.out.println("--- ACCEDIENDO A /cocina ---");
        System.out.println("Usuario en sesión: " + session.getAttribute("usuario"));

        Object objUsuario = session.getAttribute("usuario");
        if (objUsuario == null) {
            System.out.println("Redirigiendo a /login porque la sesión es nula.");
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

        dataService.cambiarEstadoPedido(id, estado);
        return "redirect:/cocina";
    }
}