package burgerflow.PNT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    @GetMapping("/carrito")
    public String verCarrito() {
        return "carrito";
    }
}
