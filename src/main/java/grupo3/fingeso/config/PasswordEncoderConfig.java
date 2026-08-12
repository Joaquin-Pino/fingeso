package grupo3.fingeso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Expone el {@link PasswordEncoder} usado para hashear contraseñas (RNF_007: nunca
 * en texto plano). Lo usa {@link DataSeeder} para los usuarios de prueba y debería
 * reutilizarlo también la configuración de autenticación (login/registro).
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
