package burgerflow.PNT.dto;

import java.util.List;

/**
 * Cuerpo del POST /api/pedidos. El total NO viene del cliente: lo calcula el servidor.
 */
public record PedidoRequest(
        String tipoEntrega,
        String direccionEntrega,
        String observaciones,
        String clienteNombre,   // solo lo usa el personal (ADMIN/EMPLEADO) en pedidos de mostrador
        List<ItemRequest> items) {

    public record ItemRequest(Long productoId, Integer cantidad) {}
}
