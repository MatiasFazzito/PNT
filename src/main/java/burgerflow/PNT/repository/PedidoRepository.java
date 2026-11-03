package burgerflow.PNT.repository;

import burgerflow.PNT.model.EstadoPedido;
import burgerflow.PNT.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Este repositorio proporciona métodos para acceder a los datos de los pedidos en la base de datos

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Pedidos en los estados indicados, del más viejo al más nuevo (cola de cocina)
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<EstadoPedido> estados);

    // Pedidos de un cliente, del más nuevo al más viejo
    List<Pedido> findByClienteEmailOrderByFechaCreacionDesc(String clienteEmail);
}
