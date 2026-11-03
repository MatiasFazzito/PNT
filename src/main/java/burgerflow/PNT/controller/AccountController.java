package burgerflow.PNT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Este controlador maneja las rutas relacionadas con la cuenta de usuario, como login, registro y administración.

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

    @GetMapping({"/mis-pedidos", "/mis-pedidos.html"})
    public String misPedidos() {
        return "mis-pedidos";
    }


    @GetMapping({"/admin", "/admin.html"})
    public String admin() {
        return "admin";
    }
}