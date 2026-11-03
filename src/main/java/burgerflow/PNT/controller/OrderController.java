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

// Este controlador maneja las rutas relacionadas con los pedidos, tanto para la vista web como para la API REST.

@Controller
public class OrderController {

    private final DataService dataService;
    private final PedidoService pedidoService;

    public OrderController(DataService dataService, PedidoService pedidoService) {
        this.dataService = dataService;
        this.pedidoService = pedidoService;
    }

    // Punto de entrada para la página web visual del carrito.
    @GetMapping({ "/carrito", "/carrito.html" })
    public String verCarrito() {
        return "carrito";
    }

    // GET: Endpoint para listar todos los pedidos (solo para la cocina).
    @GetMapping("/api/pedidos")
    @ResponseBody
    public List<Pedido> listarPedidos() {
        return dataService.getPedidos();
    }

    // POST: Endpoint para crear un nuevo pedido.
    @PostMapping("/api/pedidos")
    @ResponseBody
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest request, HttpSession session) {
        // 1. Verificamos si hay alguien con la sesión iniciada.
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para realizar un pedido"));
        }

        try {
            // 2. Intentamos crear el pedido usando el Service, que se encarga de toda la lógica de negocio.
            Pedido creado = pedidoService.crearPedido(request, usuario);
            // 3. Si todo sale bien, devolvemos un código 201 (Creado) y el ticket del pedido.
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            // 4. Si el Service falla (ej. no hay stock), atajamos el error y devolvemos un 400 (Bad Request).
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // GET: Trae el historial de compras del usuario.
    @GetMapping("/api/pedidos/mis-pedidos")
    @ResponseBody
    public ResponseEntity<?> misPedidos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para ver tus pedidos"));
        }
        // Usamos el email del usuario en sesión para buscar solo sus pedidos en la base de datos.
        return ResponseEntity.ok(pedidoService.getPedidosDeCliente(usuario.getEmail()));
    }

    // GET: Trae todo lo que haya que cocinar o entregar (ignora los entregados o cancelados).
    @GetMapping("/api/pedidos/activos")
    @ResponseBody
    public List<Pedido> pedidosActivos() {
        return pedidoService.getPedidosActivos();
    }

    // PATCH: Endpoint para que la cocina o el cajero actualicen el pedido.
    @PatchMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            EstadoPedido nuevoEstado = EstadoPedido.desdeTexto(body.get("estado"));
            return ResponseEntity.ok(pedidoService.cambiarEstado(id, nuevoEstado));
        } catch (NoSuchElementException e) {
            // Si el ID del pedido no existe en la BD.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Si intentan hacer un cambio ilógico (ej. pasar de CANCELADO a PREPARANDO).
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
