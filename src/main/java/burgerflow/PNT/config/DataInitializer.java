package burgerflow.PNT.config;

import burgerflow.PNT.model.EstadoPedido;
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
                productoRepository.save(new Producto(null, "BurgerFlow Doble Bacon",
                        "Doble carne, cheddar y bacon crocante", 8500.0, 10, "Hamburguesas", "/Images/burgerDobleCarne.jpg", true));
                productoRepository.save(new Producto(null, "Papas Cheeseburger",
                        "Papas fritas con cheddar fundido y carne", 4200.0, 15, "Acompañamientos", "/Images/papasCheddar.jpg", true));
                productoRepository.save(new Producto(null, "Gaseosa 500ml", "Línea Coca-Cola bien fría", 1800.0, 20,
                        "Bebidas", "/Images/gaseosaCocaCola.jpg", true));
            }

            // Si la base ya existía, les asigna la foto a los productos de ejemplo que no tengan imagen
            asignarImagenSiFalta(productoRepository, "BurgerFlow Doble Bacon", "/Images/burgerDobleCarne.jpg");
            asignarImagenSiFalta(productoRepository, "Papas Cheeseburger", "/Images/papasCheddar.jpg");
            asignarImagenSiFalta(productoRepository, "Gaseosa 500ml", "/Images/gaseosaCocaCola.jpg");

            // Inserta pedidos si la tabla está vacía
            if (pedidoRepository.count() == 0) {
                pedidoRepository.save(new Pedido(null, "cliente@ort.edu.ar", EstadoPedido.RECIBIDO, 12700.0, "DELIVERY"));
                pedidoRepository.save(new Pedido(null, "juan@gmail.com", EstadoPedido.EN_PREPARACION, 6000.0, "RETIRO"));
            }

            // Inserta usuarios si la tabla está vacía
            if (usuarioRepository.count() == 0) {
                // 1. Administrador
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@burgerflow.com");
                admin.setPassword("admin123");
                admin.setRol("ADMIN");
                admin.setActivo(true);
                usuarioRepository.save(admin);

                // 2. Cliente de prueba
                Usuario cliente = new Usuario();
                cliente.setNombre("Juan Pérez");
                cliente.setEmail("juan.perez@gmail.com");
                cliente.setPassword("Juan123");
                cliente.setRol("CLIENTE");
                cliente.setActivo(true);
                usuarioRepository.save(cliente);

                // 3. Empleado / Personal de cocina de prueba
                Usuario empleado = new Usuario();
                empleado.setNombre("Carlos Cocina");
                empleado.setEmail("cocina@burgerflow.com");
                empleado.setPassword("cocina123");
                empleado.setRol("EMPLEADO");
                empleado.setActivo(true);
                usuarioRepository.save(empleado);
            }
        };
    }

    private static void asignarImagenSiFalta(ProductoRepository repo, String nombre, String imagen) {
        repo.findAll().stream()
                .filter(p -> nombre.equals(p.getNombre()) && (p.getImagen() == null || p.getImagen().isBlank()))
                .forEach(p -> {
                    p.setImagen(imagen);
                    repo.save(p);
                });
    }
}