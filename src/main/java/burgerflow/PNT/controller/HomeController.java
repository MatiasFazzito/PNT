package burgerflow.PNT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Este controlador maneja la ruta principal de la aplicación, redirigiendo a la página de inicio.

@Controller
public class HomeController {

    @GetMapping({"/", "/index", "/index.html"})
    public String index() {
        return "index";
    }
}