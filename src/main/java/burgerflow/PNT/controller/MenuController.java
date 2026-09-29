package burgerflow.PNT.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import burgerflow.PNT.service.DataService;

@Controller
public class MenuController {

    @Autowired
    private DataService dataService;

    @GetMapping("/menu")
    public String verMenu(Model model) {
        model.addAttribute("productos", dataService.getProductos());
        return "menu";
    }
}
