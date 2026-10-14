package burgerflow.PNT.controller;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UserRestController {

    private final DataService dataService;

    public UserRestController(DataService dataService) {
        this.dataService = dataService;
    }

    // GET /api/usuarios
    @GetMapping
    public List<Usuario> listarUsuariosApi() {
        return dataService.getUsuarios();
    }

    // PATCH /api/usuarios/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoUsuarioApi(@PathVariable Long id) {
        dataService.cambiarEstadoUsuario(id);
        return ResponseEntity.ok().build();
    }
}