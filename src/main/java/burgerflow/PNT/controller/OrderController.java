package burgerflow.PNT.controller;

import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final DataService dataService;

    public OrderController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/carrito")
    public String verCarrito() {
        return "carrito";
    }

    @GetMapping("/mis-pedidos")
    public String verMisPedidos() {
        return "mis-pedidos";
    }

    @GetMapping("/api/pedidos")
    @ResponseBody
    public List<Pedido> listarPedidos() {
        return dataService.getPedidos();
    }

    @PatchMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<Void> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        dataService.cambiarEstadoPedido(id, nuevoEstado);
        return ResponseEntity.ok().build();
    }
}