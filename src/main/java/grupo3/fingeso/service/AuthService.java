package grupo3.fingeso.service;

import grupo3.fingeso.model.Profesor;
import grupo3.fingeso.model.Tesista;
import grupo3.fingeso.model.Usuario;
import grupo3.fingeso.repository.ProfesorRepository;
import grupo3.fingeso.repository.TesistaRepository;
import grupo3.fingeso.repository.UsuarioRepository;
import grupo3.fingeso.dto.AuthResponse;
import grupo3.fingeso.dto.LoginRequest;
import grupo3.fingeso.dto.RegisterRequest;
import grupo3.fingeso.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final TesistaRepository tesistaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_TESISTA");

        String token = jwtService.generateToken(authentication.getName(), role);

        return new AuthResponse(token);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario;

        switch (request.rol().toUpperCase()) {
            case "TESISTA" -> {
                Tesista tesista = Tesista.builder()
                        .nombre(request.nombre())
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .carrera("Sin carrera")
                        .build();

                usuario = tesistaRepository.save(tesista);
            }
            case "PROFESOR" -> {
                Profesor profesor = Profesor.builder()
                        .nombre(request.nombre())
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .departamento("Sin departamento")
                        .build();

                usuario = profesorRepository.save(profesor);
            }
            default -> throw new IllegalArgumentException("Tipo de usuario inválido");
        }

        String role = (usuario instanceof Profesor) ? "ROLE_PROFESOR" : "ROLE_TESISTA";
        String token = jwtService.generateToken(usuario.getEmail(), role);

        return new AuthResponse(token);
    }
}
