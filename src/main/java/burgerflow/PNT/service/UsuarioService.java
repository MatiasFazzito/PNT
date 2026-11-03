package burgerflow.PNT.service;

import burgerflow.PNT.model.Usuario;
import burgerflow.PNT.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        // Actualizamos los campos que vengan informados
        if (usuarioActualizado.getRol() != null) {
            usuario.setRol(usuarioActualizado.getRol());
        }
        if (usuarioActualizado.getNombre() != null) {
            usuario.setNombre(usuarioActualizado.getNombre());
        }
        if (usuarioActualizado.getEmail() != null) {
            usuario.setEmail(usuarioActualizado.getEmail());
        }
        if (usuarioActualizado.getActivo() != null) {
            usuario.setActivo(usuarioActualizado.getActivo());
        }
        
        return usuarioRepository.save(usuario);
    }

    public void cambiarEstadoUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(u.getActivo() == null ? false : !u.getActivo());
            usuarioRepository.save(u);
        });
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorEmailAndPassword(String email, String password) {
        return usuarioRepository.findByEmailAndPassword(email, password);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}