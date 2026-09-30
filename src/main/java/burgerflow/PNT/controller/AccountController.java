package burgerflow.PNT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "login";
    }

    @GetMapping({"/registro", "/registro.html"})
    public String registro() {
        return "registro";
    }

    // ✅ Mapeo para Mis Pedidos
    @GetMapping({"/mis-pedidos", "/mis-pedidos.html"})
    public String misPedidos() {
        return "mis-pedidos"; // Devuelve templates/mis-pedidos.html
    }

    // ✅ Mapeo para el Panel de Administración
    @GetMapping({"/admin", "/admin.html"})
    public String admin() {
        return "admin"; // Devuelve templates/admin.html
    }
}