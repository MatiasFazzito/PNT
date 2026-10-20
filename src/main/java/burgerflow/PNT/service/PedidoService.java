package burgerflow.PNT.service;

import burgerflow.PNT.dto.PedidoRequest;
import burgerflow.PNT.model.DetallePedido;
import burgerflow.PNT.model.EstadoPedido;
import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.model.Producto;
import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.repository.PedidoRepository;
import burgerflow.PNT.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    // Inyección de dependencias: conectamos los repositorios para poder hablar con la Base de Datos.
    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    // @Transactional asegura que si algo falla (ej. no hay stock de una hamburguesa), 
    // toda la operación se cancela y no se guarda un pedido a medias en la base de datos.
    @Transactional
    public Pedido crearPedido(PedidoRequest request, Usuario usuario) {
        // 1. Validación básica: no me podés mandar un pedido fantasma sin productos.
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }

        // 2. Validamos el tipo de entrega según quién está operando el sistema.
        String tipoEntrega = request.tipoEntrega() == null ? "" : request.tipoEntrega().trim().toUpperCase();
        boolean esPersonal = esPersonal(usuario);
        
        // Si es un cliente normal, solo puede pedir DELIVERY o RETIRO. 
        // Si es empleado/admin, se le habilita la opción MOSTRADOR.
        boolean tipoValido = tipoEntrega.equals("DELIVERY") || tipoEntrega.equals("RETIRO")
                || (esPersonal && tipoEntrega.equals("MOSTRADOR"));
        if (!tipoValido) {
            throw new IllegalArgumentException(esPersonal
                    ? "El tipo de entrega debe ser MOSTRADOR, RETIRO o DELIVERY"
                    : "El tipo de entrega debe ser DELIVERY o RETIRO");
        }
        
        // Si pidió delivery, sí o sí tiene que haber puesto una dirección.
        String direccion = request.direccionEntrega() == null ? "" : request.direccionEntrega().trim();
        if (tipoEntrega.equals("DELIVERY") && direccion.isEmpty()) {
            throw new IllegalArgumentException("Ingresá una dirección para el delivery");
        }

        Pedido pedido = new Pedido();
        
        // 3. Acá armamos el perfil de quién hace el pedido.
        if (esPersonal) {
            // Si el que carga el pedido es el cajero, tomamos el nombre del cliente que le dictó en el local.
            String nombre = request.clienteNombre() == null ? "" : request.clienteNombre().trim();
            if (nombre.isEmpty()) {
                throw new IllegalArgumentException("Ingresá el nombre del cliente");
            }
            if (nombre.length() > 100) {
                throw new IllegalArgumentException("El nombre del cliente es demasiado largo (máximo 100 caracteres)");
            }
            pedido.setClienteNombre(nombre);
            pedido.setClienteEmail(null);
            pedido.setCargadoPor(usuario.getEmail()); // Dejamos registro de qué empleado lo cobró.
        } else {
            // Si es un pedido online, sacamos los datos directamente del usuario que inició sesión.
            pedido.setClienteNombre(usuario.getNombre());
            pedido.setClienteEmail(usuario.getEmail());
        }
        
        // Seteamos los datos iniciales por defecto.
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setDireccionEntrega(tipoEntrega.equals("DELIVERY") ? direccion : null);
        pedido.setObservaciones(request.observaciones() == null || request.observaciones().isBlank()
                ? null : request.observaciones().trim());

        double total = 0;
        
        // 4. Procesamos el carrito: iteramos sobre cada ítem que mandaron en el request.
        for (PedidoRequest.ItemRequest item : request.items()) {
            if (item.productoId() == null || item.cantidad() == null || item.cantidad() <= 0) {
                throw new IllegalArgumentException("Cada ítem necesita productoId y una cantidad mayor a 0");
            }

            // Buscamos el producto en la BD. Si no existe, cortamos todo.
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto " + item.productoId() + " no existe"));

            // Nos aseguramos de que no estén pidiendo algo que sacamos del menú.
            if (Boolean.FALSE.equals(producto.getActivo())) {
                throw new IllegalArgumentException("El producto \"" + producto.getNombre() + "\" no está disponible");
            }

            // 5. Control de Stock.
            int stock = producto.getStock() == null ? 0 : producto.getStock();
            if (stock < item.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente de \"" + producto.getNombre()
                        + "\" (disponible: " + stock + ")");
            }

            // Descontamos lo que compraron del stock real y lo guardamos.
            producto.setStock(stock - item.cantidad());
            productoRepository.save(producto);

            // 6. Armamos el "ticket" (DetallePedido) de este producto y lo sumamos al total del pedido.
            DetallePedido detalle = new DetallePedido(producto.getId(), producto.getNombre(),
                    item.cantidad(), producto.getPrecio());
            pedido.agregarItem(detalle);
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);
        // Guardamos el pedido finalizado en la Base de Datos y lo devolvemos.
        return pedidoRepository.save(pedido);
    }

    // Método auxiliar (privado) para saber rápido si el usuario es del staff.
    private boolean esPersonal(Usuario usuario) {
        return "ADMIN".equalsIgnoreCase(usuario.getRol()) || "EMPLEADO".equalsIgnoreCase(usuario.getRol());
    }

    /**
     * Cambia el estado validando que la transición sea lógica (ej. no pasar de ENTREGADO a PREPARANDO).
     * Si el pedido se cancela por algún motivo, ejecuta el reintegro de stock automático.
     */
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        // Buscamos el pedido.
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El pedido " + id + " no existe"));

        EstadoPedido actual = pedido.getEstado();
        // Validamos la lógica de negocio: ¿Tiene sentido este cambio de estado?
        if (actual == null || !actual.puedePasarA(nuevoEstado)) {
            throw new IllegalArgumentException("No se puede pasar de " + actual + " a " + nuevoEstado);
        }

        // Si se cancela la orden, llamamos al método que devuelve las hamburguesas a la heladera (al stock).
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            reponerStock(pedido);
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    // Recorre los ítems del pedido cancelado y le vuelve a sumar la cantidad al stock del producto original.
    private void reponerStock(Pedido pedido) {
        for (DetallePedido item : pedido.getItems()) {
            productoRepository.findById(item.getProductoId()).ifPresent(producto -> {
                int stock = producto.getStock() == null ? 0 : producto.getStock();
                producto.setStock(stock + item.getCantidad());
                productoRepository.save(producto);
            });
        }
    }

    // (readOnly = true) optimiza la consulta porque le avisa a Spring que no vamos a modificar nada, solo a leer.
    // Trae los pedidos para la vista de la cocina (los que no están cancelados ni entregados).
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosActivos() {
        return pedidoRepository.findByEstadoInOrderByFechaCreacionAsc(
                Arrays.stream(EstadoPedido.values()).filter(EstadoPedido::esActivo).toList());
    }

    // Trae el historial de pedidos de un cliente específico, ordenados del más nuevo al más viejo.
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosDeCliente(String email) {
        return pedidoRepository.findByClienteEmailOrderByFechaCreacionDesc(email);
    }
}