package burgerflow.PNT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import burgerflow.PNT.service.DataService;

@Controller
public class MenuController {

    private final DataService dataService;

    public MenuController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/menu")
    public String verMenu(Model model) {
        model.addAttribute("productos", dataService.getProductos());
        return "menu";
    }
}