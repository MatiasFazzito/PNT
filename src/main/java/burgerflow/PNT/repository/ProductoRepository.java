package burgerflow.PNT.repository;

import burgerflow.PNT.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

// Este repositorio proporciona métodos para acceder a los datos de los productos en la base de datos

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}