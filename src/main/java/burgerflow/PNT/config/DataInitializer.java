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
                // --- CATEGORÍA: HAMBURGUESAS ---
                productoRepository.save(new Producto(null, "BurgerFlow Doble Bacon",
                        "Doble carne, cheddar y bacon crocante", 8500.0, 10, "Hamburguesas",
                        "/Images/burgerDobleCarne.jpg", true));
                productoRepository.save(new Producto(null, "BurgerFlow Mega Triple",
                        "Triple carne, triple cheddar, bacon y salsa especial de la casa", 11000.0, 8, "Hamburguesas",
                        "/Images/burgerDobleCarne.jpg", true));
                productoRepository.save(new Producto(null, "BurgerFlow Crispy Chicken",
                        "Pollo crispy, lechuga fresca, tomate y mayonesa al ajo", 7800.0, 12, "Hamburguesas",
                        "/Images/burgerDobleCarne.jpg", true));
                productoRepository.save(new Producto(null, "BurgerFlow Veggie Mushroom",
                        "Medallón de legumbres, hongos portobello salteados y queso vegano", 8200.0, 15, "Hamburguesas",
                        "/Images/burgerDobleCarne.jpg", true));
                productoRepository.save(new Producto(null, "BurgerFlow Clásica Simple",
                        "Medallón de carne seleccionado, cheddar fundido, lechuga y tomate", 6500.0, 20, "Hamburguesas",
                        "/Images/burgerDobleCarne.jpg", true));

                // --- CATEGORÍA: ACOMPAÑAMIENTOS ---
                productoRepository.save(new Producto(null, "Papas Cheeseburger",
                        "Papas fritas con cheddar fundido y panceta picada", 4200.0, 15, "Acompañamientos",
                        "/Images/papasCheddar.jpg", true));
                productoRepository.save(new Producto(null, "Papas Fritas Clásicas",
                        "Porción grande de papas doradas, crujientes y sal marina", 3500.0, 25, "Acompañamientos",
                        "/Images/papasCheddar.jpg", true));
                productoRepository.save(new Producto(null, "Aros de Cebolla Rebozados",
                        "Aros de cebolla crujientes acompañados de salsa tártara", 3900.0, 18, "Acompañamientos",
                        "/Images/papasCheddar.jpg", true));
                productoRepository.save(new Producto(null, "Bastones de Mozzarella",
                        "6 bastones empanados con orégano y salsa marinara", 4800.0, 10, "Acompañamientos",
                        "/Images/papasCheddar.jpg", true));
                productoRepository.save(new Producto(null, "Nuggets de Pollo Crocantes",
                        "8 unidades de pollo tierno con salsa barbacoa", 4100.0, 20, "Acompañamientos",
                        "/Images/papasCheddar.jpg", true));

                // --- CATEGORÍA: BEBIDAS ---
                productoRepository.save(new Producto(null, "Gaseosa 500ml",
                        "Línea Coca-Cola bien fría", 1800.0, 30, "Bebidas",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Limonada con Menta y Jengibre",
                        "Vaso de 500ml refrescante y natural", 2200.0, 20, "Bebidas",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Cerveza Artesanal IPA 500ml",
                        "Pinta rubia artesanal bien fría", 3200.0, 15, "Bebidas",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Agua Mineral 500ml",
                        "Agua sin gas o con gas", 1500.0, 40, "Bebidas",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Jugo de Naranja Natural",
                        "Exprimido fresco del día (400ml)", 2500.0, 12, "Bebidas",
                        "/Images/gaseosaCocaCola.jpg", true));

                // --- CATEGORÍA: POSTRES ---
                productoRepository.save(new Producto(null, "Helado de Dulce de Leche Granizado",
                        "Copa artesanal de 2 bochas", 2800.0, 15, "Postres",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Volcán de Chocolate",
                        "Torta húmeda tibia con corazón de chocolate fundido y bocha de helado", 3800.0, 10, "Postres",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Cheesecake de Frutos Rojos",
                        "Porción clásica con coulis de frutos rojos", 3600.0, 8, "Postres",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Brownie con Nueces",
                        "Cuadrado de chocolate intenso con nueces tostadas", 2900.0, 12, "Postres",
                        "/Images/gaseosaCocaCola.jpg", true));
                productoRepository.save(new Producto(null, "Milkshake de Oreo",
                        "Batido espeso de vainilla con trozos de galletitas", 3400.0, 14, "Postres",
                        "/Images/gaseosaCocaCola.jpg", true));
            }

            // Asigna imagen por defecto si algún producto quedó sin ella en bases existentes
            asignarImagenSiFalta(productoRepository, "BurgerFlow Doble Bacon", "/Images/burgerDobleCarne.jpg");
            asignarImagenSiFalta(productoRepository, "Papas Cheeseburger", "/Images/papasCheddar.jpg");
            asignarImagenSiFalta(productoRepository, "Gaseosa 500ml", "/Images/gaseosaCocaCola.jpg");

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

                // 2. Personal de Cocina
                Usuario empleado1 = new Usuario();
                empleado1.setNombre("Carlos Cocina");
                empleado1.setEmail("cocina@burgerflow.com");
                empleado1.setPassword("cocina123");
                empleado1.setRol("EMPLEADO");
                empleado1.setActivo(true);
                usuarioRepository.save(empleado1);

                // 3. Personal de Caja
                Usuario empleado2 = new Usuario();
                empleado2.setNombre("Sofía Cajera");
                empleado2.setEmail("caja@burgerflow.com");
                empleado2.setPassword("caja123");
                empleado2.setRol("EMPLEADO");
                empleado2.setActivo(true);
                usuarioRepository.save(empleado2);

                // 4. Clientes
                Usuario cliente1 = new Usuario();
                cliente1.setNombre("Juan Pérez");
                cliente1.setEmail("juan.perez@gmail.com");
                cliente1.setPassword("Juan123");
                cliente1.setRol("CLIENTE");
                cliente1.setActivo(true);
                usuarioRepository.save(cliente1);

                Usuario cliente2 = new Usuario();
                cliente2.setNombre("María Gómez");
                cliente2.setEmail("maria.gomez@gmail.com");
                cliente2.setPassword("Maria123");
                cliente2.setRol("CLIENTE");
                cliente2.setActivo(true);
                usuarioRepository.save(cliente2);

                Usuario cliente3 = new Usuario();
                cliente3.setNombre("Esteban Quito");
                cliente3.setEmail("esteban@gmail.com");
                cliente3.setPassword("Esteban123");
                cliente3.setRol("CLIENTE");
                cliente3.setActivo(true);
                usuarioRepository.save(cliente3);
            }

            // Inserta pedidos en distintos estados si la tabla está vacía
            if (pedidoRepository.count() == 0) {
                pedidoRepository.save(new Pedido(null, "juan.perez@gmail.com", EstadoPedido.RECIBIDO, 12700.0, "DELIVERY"));
                pedidoRepository.save(new Pedido(null, "maria.gomez@gmail.com", EstadoPedido.EN_PREPARACION, 11000.0, "RETIRO"));
                pedidoRepository.save(new Pedido(null, "esteban@gmail.com", EstadoPedido.LISTO_PARA_ENTREGAR, 7800.0, "MOSTRADOR"));
                pedidoRepository.save(new Pedido(null, "juan.perez@gmail.com", EstadoPedido.ENTREGADO, 15300.0, "DELIVERY"));
                pedidoRepository.save(new Pedido(null, "maria.gomez@gmail.com", EstadoPedido.CANCELADO, 4200.0, "RETIRO"));
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