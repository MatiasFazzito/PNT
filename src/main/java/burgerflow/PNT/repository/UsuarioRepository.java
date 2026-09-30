package burgerflow.PNT.repository;

import burgerflow.PNT.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email (para verificar existencia y login)
    Optional<Usuario> findByEmail(String email);

    // Validar si un email ya está registrado (ideal para el proceso de Registro)
    boolean existsByEmail(String email);

    // Buscar por email y contraseña (autenticación manual sin Spring Security)
    Optional<Usuario> findByEmailAndPassword(String email, String password);

    // Obtener lista de usuarios filtrados por rol (CLIENTE, EMPLEADO, ADMIN)
    List<Usuario> findByRol(String rol);
}