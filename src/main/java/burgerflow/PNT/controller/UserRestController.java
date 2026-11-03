package burgerflow.PNT.controller;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Este controlador maneja las rutas relacionadas con los usuarios, proporcionando endpoints REST para listar usuarios y cambiar su estado.

@RestController
@RequestMapping("/api/usuarios")
public class UserRestController {

    private final DataService dataService;

    public UserRestController(DataService dataService) {
        this.dataService = dataService;
    }

    // GET: Devuelve la lista de todos los usuarios.
    @GetMapping
    public List<Usuario> listarUsuariosApi() {
        return dataService.getUsuarios();
    }

    // PATCH: Endpoint para cambiar el estado de un usuario (activo/inactivo).
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoUsuarioApi(@PathVariable Long id) {
        dataService.cambiarEstadoUsuario(id);
        return ResponseEntity.ok().build();
    }
}