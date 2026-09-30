package burgerflow.PNT.controller;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UsuarioRepository usuarioRepository;

    public AuthRestController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Debe proporcionar email y password"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndPassword(email, password);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales incorrectas"));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        // Validar que el email no esté registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "El email ya se encuentra registrado"));
        }

        // Asignar rol CLIENTE por defecto si no viene especificado
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
}