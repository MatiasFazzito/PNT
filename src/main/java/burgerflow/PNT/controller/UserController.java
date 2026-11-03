package burgerflow.PNT.controller;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Este controlador maneja las rutas relacionadas con los usuarios, proporcionando endpoints REST para listar usuarios y cambiar su estado.

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UsuarioService usuarioService;

    public UserController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET: Devuelve la lista de todos los usuarios.
    @GetMapping
    public List<Usuario> listarUsuariosApi() {
        return usuarioService.listarUsuarios();
    }

    // PATCH: Endpoint para cambiar el estado de un usuario (activo/inactivo).
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoUsuarioApi(@PathVariable Long id) {
        usuarioService.cambiarEstadoUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuarioApi(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        return ResponseEntity.ok(actualizado);
    }
}