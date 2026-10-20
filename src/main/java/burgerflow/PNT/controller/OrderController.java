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

    // Inyección de dependencias: traemos los servicios que tienen la lógica pesada.
    public OrderController(DataService dataService, PedidoService pedidoService) {
        this.dataService = dataService;
        this.pedidoService = pedidoService;
    }

    // Punto de entrada para la página web visual del carrito.
    // (/mis-pedidos se mapea en AccountController)
    @GetMapping({"/carrito", "/carrito.html"})
    public String verCarrito() {
        return "carrito";
    }

    // Endpoint básico para traer el listado completo de pedidos.
    @GetMapping("/api/pedidos")
    @ResponseBody
    public List<Pedido> listarPedidos() {
        return dataService.getPedidos();
    }

    // CREAR PEDIDO: Este es el endpoint que recibe el POST desde el fetch() del carrito.
    @PostMapping("/api/pedidos")
    @ResponseBody
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest request, HttpSession session) {
        // 1. Verificamos si hay alguien con la sesión iniciada.
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            // Si es un visitante fantasma, le cortamos el rostro con un error 401 (No autorizado).
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Iniciá sesión para realizar un pedido"));
        }
        
        try {
            // 2. Le pasamos la pelota al PedidoService para que valide stock, precios, etc.
            Pedido creado = pedidoService.crearPedido(request, usuario);
            // 3. Si todo sale bien, devolvemos un código 201 (Creado) y el ticket del pedido.
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            // 4. Si el Service falla (ej. no hay stock), atajamos el error y devolvemos un 400 (Bad Request).
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // PEDIDOS DEL CLIENTE LOGUEADO: Trae el historial de compras del usuario.
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

    // PEDIDOS ACTIVOS: Este endpoint lo va a consumir la pantalla de la cocina.
    // Trae todo lo que haya que cocinar o entregar (ignora los entregados o cancelados).
    @GetMapping("/api/pedidos/activos")
    @ResponseBody
    public List<Pedido> pedidosActivos() {
        return pedidoService.getPedidosActivos();
    }

    // CAMBIAR ESTADO: Endpoint para que la cocina o el cajero avancen el pedido 
    // (ej. de RECIBIDO a PREPARANDO, o a CANCELADO).
    @PatchMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            // Extraemos el texto del JSON que nos manda el frontend (ej. {"estado": "LISTO"})
            EstadoPedido nuevoEstado = EstadoPedido.desdeTexto(body.get("estado"));
            // Ejecutamos el cambio en el Service.
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
