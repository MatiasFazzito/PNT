package burgerflow.PNT.config;

import burgerflow.PNT.model.Pedido;
import burgerflow.PNT.model.Producto;
import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.repository.PedidoRepository;
import burgerflow.PNT.repository.ProductoRepository;
import burgerflow.PNT.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {
        return args -> {
            // Inserta productos si la tabla está vacía
            if (productoRepository.count() == 0) {
                productoRepository.save(new Producto(null, "BurgerFlow Doble Bacon", "Doble carne, cheddar y bacon crocante", 8500.0, 10, "Hamburguesas"));
                productoRepository.save(new Producto(null, "Papas Cheeseburger", "Papas fritas con cheddar fundido y carne", 4200.0, 15, "Acompañamientos"));
                productoRepository.save(new Producto(null, "Gaseosa 500ml", "Línea Coca-Cola bien fría", 1800.0, 20, "Bebidas"));
            }

            // Inserta pedidos si la tabla está vacía
            if (pedidoRepository.count() == 0) {
                pedidoRepository.save(new Pedido(null, "cliente@ort.edu.ar", "Pendiente", 12700.0, "Delivery"));
                pedidoRepository.save(new Pedido(null, "juan@gmail.com", "En preparación", 6000.0, "Retiro"));
            }

            // Inserta usuario Administrador si la tabla está vacía
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@burgerflow.com");
                admin.setPassword("admin123");
                admin.setRol("ADMIN");
                usuarioRepository.save(admin);
            }
        };
    }
}