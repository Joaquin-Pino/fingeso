package grupo3.fingeso.security;

import grupo3.fingeso.model.Profesor;
import grupo3.fingeso.model.Tesista;
import grupo3.fingeso.model.Usuario;
import grupo3.fingeso.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        String role = mapRole(usuario);

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(List.of (new SimpleGrantedAuthority(role)))
                .build();
    }

    private String mapRole(Usuario usuario) {
        if (usuario instanceof Tesista) {
            return "ROLE_TESISTA";
        } else if (usuario instanceof Profesor) {
            return "ROLE_PROFESOR";
        } else {
            throw new IllegalArgumentException("Tipo de usuario desconocido: " + usuario.getClass().getSimpleName());
        }
    }
}