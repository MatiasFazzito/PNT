package burgerflow.PNT.dto;

import java.util.List;

/**
 * Cuerpo del POST /api/pedidos. El total NO viene del cliente: lo calcula el servidor.
 */
public record PedidoRequest(
        String tipoEntrega,
        String direccionEntrega,
        String observaciones,
        String clienteNombre,
        List<ItemRequest> items) {

    public record ItemRequest(Long productoId, Integer cantidad) {}
}
