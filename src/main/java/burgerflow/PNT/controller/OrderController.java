package burgerflow.PNT.controller;

import burgerflow.PNT.dto.PedidoRequest;
import burgerflow.PNT.model.EstadoPedido;
import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.DataService;
import burgerflow.PNT.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
public class OrderController {

    private final DataService dataService;
    private final PedidoService pedidoService;

    public OrderController(DataService dataService, PedidoService pedidoService) {
        this.dataService = dataService;
        this.pedidoService = pedidoService;
    }

    // (/mis-pedidos se mapea en AccountController)
    @GetMapping({"/carrito", "/carrito.html"})
    public String verCarrito() {
        return "carrito";
    }

    @GetMapping("/api/pedidos")
    @ResponseBody
    public List<Pedido> listarPedidos() {
        return dataService.getPedidos();
    }

    // CREAR PEDIDO (cliente logueado)
    @PostMapping("/api/pedidos")
    @ResponseBody
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest request, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para realizar un pedido"));
        }
        try {
            Pedido creado = pedidoService.crearPedido(request, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // PEDIDOS DEL CLIENTE LOGUEADO
    @GetMapping("/api/pedidos/mis-pedidos")
    @ResponseBody
    public ResponseEntity<?> misPedidos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para ver tus pedidos"));
        }
        return ResponseEntity.ok(pedidoService.getPedidosDeCliente(usuario.getEmail()));
    }

    // PEDIDOS ACTIVOS (cola de cocina): todo lo que no está entregado ni cancelado
    @GetMapping("/api/pedidos/activos")
    @ResponseBody
    public List<Pedido> pedidosActivos() {
        return pedidoService.getPedidosActivos();
    }

    // CAMBIAR ESTADO (valida la transición y repone stock si se cancela)
    @PatchMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            EstadoPedido nuevoEstado = EstadoPedido.desdeTexto(body.get("estado"));
            return ResponseEntity.ok(pedidoService.cambiarEstado(id, nuevoEstado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
