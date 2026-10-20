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

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Pedido crearPedido(PedidoRequest request, Usuario usuario) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }

        String tipoEntrega = request.tipoEntrega() == null ? "" : request.tipoEntrega().trim().toUpperCase();
        boolean esPersonal = esPersonal(usuario);
        boolean tipoValido = tipoEntrega.equals("DELIVERY") || tipoEntrega.equals("RETIRO")
                || (esPersonal && tipoEntrega.equals("MOSTRADOR"));
        if (!tipoValido) {
            throw new IllegalArgumentException(esPersonal
                    ? "El tipo de entrega debe ser MOSTRADOR, RETIRO o DELIVERY"
                    : "El tipo de entrega debe ser DELIVERY o RETIRO");
        }
        String direccion = request.direccionEntrega() == null ? "" : request.direccionEntrega().trim();
        if (tipoEntrega.equals("DELIVERY") && direccion.isEmpty()) {
            throw new IllegalArgumentException("Ingresá una dirección para el delivery");
        }

        Pedido pedido = new Pedido();
        if (esPersonal) {
            // Pedido de mostrador/teléfono: lo carga el personal a nombre de un cliente
            String nombre = request.clienteNombre() == null ? "" : request.clienteNombre().trim();
            if (nombre.isEmpty()) {
                throw new IllegalArgumentException("Ingresá el nombre del cliente");
            }
            if (nombre.length() > 100) {
                throw new IllegalArgumentException("El nombre del cliente es demasiado largo (máximo 100 caracteres)");
            }
            pedido.setClienteNombre(nombre);
            pedido.setClienteEmail(null);
            pedido.setCargadoPor(usuario.getEmail());
        } else {
            // Pedido online: el cliente es el usuario logueado (se ignora cualquier clienteNombre enviado)
            pedido.setClienteNombre(usuario.getNombre());
            pedido.setClienteEmail(usuario.getEmail());
        }
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setDireccionEntrega(tipoEntrega.equals("DELIVERY") ? direccion : null);
        pedido.setObservaciones(request.observaciones() == null || request.observaciones().isBlank()
                ? null : request.observaciones().trim());

        double total = 0;
        for (PedidoRequest.ItemRequest item : request.items()) {
            if (item.productoId() == null || item.cantidad() == null || item.cantidad() <= 0) {
                throw new IllegalArgumentException("Cada ítem necesita productoId y una cantidad mayor a 0");
            }

            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto " + item.productoId() + " no existe"));

            if (Boolean.FALSE.equals(producto.getActivo())) {
                throw new IllegalArgumentException("El producto \"" + producto.getNombre() + "\" no está disponible");
            }

            int stock = producto.getStock() == null ? 0 : producto.getStock();
            if (stock < item.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente de \"" + producto.getNombre()
                        + "\" (disponible: " + stock + ")");
            }

            producto.setStock(stock - item.cantidad());
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido(producto.getId(), producto.getNombre(),
                    item.cantidad(), producto.getPrecio());
            pedido.agregarItem(detalle);
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    private boolean esPersonal(Usuario usuario) {
        return "ADMIN".equalsIgnoreCase(usuario.getRol()) || "EMPLEADO".equalsIgnoreCase(usuario.getRol());
    }

    /**
     * Cambia el estado validando que la transición sea permitida.
     * Si el pedido se cancela, devuelve el stock de sus ítems.
     *
     * @throws NoSuchElementException   si el pedido no existe
     * @throws IllegalArgumentException si la transición no es válida
     */
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El pedido " + id + " no existe"));

        EstadoPedido actual = pedido.getEstado();
        if (actual == null || !actual.puedePasarA(nuevoEstado)) {
            throw new IllegalArgumentException("No se puede pasar de " + actual + " a " + nuevoEstado);
        }

        if (nuevoEstado == EstadoPedido.CANCELADO) {
            reponerStock(pedido);
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    private void reponerStock(Pedido pedido) {
        for (DetallePedido item : pedido.getItems()) {
            productoRepository.findById(item.getProductoId()).ifPresent(producto -> {
                int stock = producto.getStock() == null ? 0 : producto.getStock();
                producto.setStock(stock + item.getCantidad());
                productoRepository.save(producto);
            });
        }
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidosActivos() {
        return pedidoRepository.findByEstadoInOrderByFechaCreacionAsc(
                Arrays.stream(EstadoPedido.values()).filter(EstadoPedido::esActivo).toList());
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidosDeCliente(String email) {
        return pedidoRepository.findByClienteEmailOrderByFechaCreacionDesc(email);
    }
}
