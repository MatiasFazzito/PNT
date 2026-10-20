package burgerflow.PNT.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados posibles de un pedido y el flujo permitido entre ellos:
 * RECIBIDO -> EN_PREPARACION -> LISTO_PARA_ENTREGAR -> ENTREGADO
 * y, mientras no esté entregado, cualquiera puede pasar a CANCELADO.
 */
public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO_PARA_ENTREGAR,
    ENTREGADO,
    CANCELADO;

    public Set<EstadoPedido> siguientesPermitidos() {
        return switch (this) {
            case RECIBIDO -> EnumSet.of(EN_PREPARACION, CANCELADO);
            case EN_PREPARACION -> EnumSet.of(LISTO_PARA_ENTREGAR, CANCELADO);
            case LISTO_PARA_ENTREGAR -> EnumSet.of(ENTREGADO, CANCELADO);
            case ENTREGADO, CANCELADO -> EnumSet.noneOf(EstadoPedido.class);
        };
    }

    public boolean puedePasarA(EstadoPedido nuevo) {
        return siguientesPermitidos().contains(nuevo);
    }

    // Un pedido activo es el que cocina todavía tiene que atender
    public boolean esActivo() {
        return this != ENTREGADO && this != CANCELADO;
    }

    public static EstadoPedido desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Falta el estado del pedido");
        }
        try {
            return EstadoPedido.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + texto);
        }
    }
}
