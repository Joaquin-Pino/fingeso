package grupo3.fingeso.config;

import grupo3.fingeso.model.EntregaAvance;
import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.HistorialCambio;
import grupo3.fingeso.model.Profesor;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.model.Tesista;
import grupo3.fingeso.model.TipoCambio;
import grupo3.fingeso.repository.EntregaAvanceRepository;
import grupo3.fingeso.repository.HistorialCambioRepository;
import grupo3.fingeso.repository.ProfesorRepository;
import grupo3.fingeso.repository.TesisRepository;
import grupo3.fingeso.repository.TesistaRepository;
import grupo3.fingeso.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Carga datos de prueba (profesores, tesistas, tesis) al iniciar la aplicación,
 * solo si la base de datos está vacía. Reemplaza CU-03 (Registrar Tesis), que no
 * se implementa en este alcance: las tesis se cargan directamente acá.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    /** Contraseña compartida por todos los usuarios de prueba (solo para desarrollo). */
    private static final String PASSWORD_PRUEBA = "Password123!";

    private final UsuarioRepository usuarioRepository;
    private final TesistaRepository tesistaRepository;
    private final ProfesorRepository profesorRepository;
    private final TesisRepository tesisRepository;
    private final EntregaAvanceRepository entregaAvanceRepository;
    private final HistorialCambioRepository historialCambioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("DataSeeder: ya existen usuarios, se omite la carga de datos de prueba.");
            return;
        }

        String hash = passwordEncoder.encode(PASSWORD_PRUEBA);

        Profesor anaRojas = profesorRepository.save(Profesor.builder()
                .nombre("Ana Rojas")
                .email("ana.rojas@usach.cl")
                .password(hash)
                .departamento("Ingeniería Informática")
                .build());

        Profesor carlosMunoz = profesorRepository.save(Profesor.builder()
                .nombre("Carlos Muñoz")
                .email("carlos.munoz@usach.cl")
                .password(hash)
                .departamento("Ingeniería Informática")
                .build());

        Tesista diego = tesistaRepository.save(Tesista.builder()
                .nombre("Diego Fernández")
                .email("diego.fernandez@usach.cl")
                .password(hash)
                .carrera("Ingeniería Civil en Informática")
                .build());

        Tesista valentina = tesistaRepository.save(Tesista.builder()
                .nombre("Valentina Soto")
                .email("valentina.soto@usach.cl")
                .password(hash)
                .carrera("Ingeniería Civil en Informática")
                .build());

        Tesista martin = tesistaRepository.save(Tesista.builder()
                .nombre("Martín Herrera")
                .email("martin.herrera@usach.cl")
                .password(hash)
                .carrera("Ingeniería Civil en Informática")
                .build());

        Tesis tesisDiego = tesisRepository.save(Tesis.builder()
                .titulo("Plataforma de Gestión de Tesistas")
                .tesista(diego)
                .profesorGuia(anaRojas)
                .profesorCoguia(carlosMunoz)
                .estado(EstadoTesis.EN_DESARROLLO)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .build());

        tesisRepository.save(Tesis.builder()
                .titulo("Sistema de recomendación de temas de tesis")
                .tesista(valentina)
                .profesorGuia(carlosMunoz)
                .estado(EstadoTesis.EN_DESARROLLO)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .build());

        tesisRepository.save(Tesis.builder()
                .titulo("Análisis de datos académicos para detección temprana de riesgo")
                .tesista(martin)
                .profesorGuia(anaRojas)
                .estado(EstadoTesis.HABILITADA_PARA_DEFENSA)
                .fechaInicio(LocalDate.of(2025, 8, 1))
                .build());

        entregaAvanceRepository.save(EntregaAvance.builder()
                .tesis(tesisDiego)
                .nombreArchivo("avance_1_diego.pdf")
                .rutaArchivo("/storage/entregas/avance_1_diego.pdf")
                .tamanioBytes(1_200_000L)
                .build());

        historialCambioRepository.save(HistorialCambio.builder()
                .tesis(tesisDiego)
                .usuario(anaRojas)
                .tipoCambio(TipoCambio.CAMBIO_ESTADO)
                .descripcion("Tesis registrada y habilitada para desarrollo.")
                .build());

        log.info("DataSeeder: datos de prueba cargados (contraseña de prueba para todos: '{}').", PASSWORD_PRUEBA);
    }
}
