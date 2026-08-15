package grupo3.fingeso.service;

import grupo3.fingeso.model.EntregaAvance;
import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.model.TipoCambio;
import grupo3.fingeso.repository.EntregaAvanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregaAvanceService {

    private final EntregaAvanceRepository entregaAvanceRepository;
    private final TesisService tesisService;
    private final HistorialCambioService historialCambioService;
    private final NotificacionService notificacionService;

    // Ruta coherente con los datos de prueba del DataSeeder
    private static final String DIRECTORIO_STORAGE = "/storage/entregas/";
    // 20 MB en bytes
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @Transactional
    public EntregaAvance registrarEntrega(Long tesisId, MultipartFile archivo, String comentario) throws IOException {

        // 1. Validar que la tesis existe utilizando el servicio de la Persona 3
        Tesis tesis = tesisService.obtenerTesisPorId(tesisId)
                .orElseThrow(() -> new IllegalArgumentException("La tesis con ID " + tesisId + " no existe."));

        // 2. Validar que la tesis esté EN_DESARROLLO (OP-07)
        if (tesis.getEstado() != EstadoTesis.EN_DESARROLLO) {
            throw new IllegalStateException("No se pueden subir avances. La tesis no se encuentra EN_DESARROLLO.");
        }

        // 3. Validar que el archivo sea PDF (OP-08)
        if (archivo.getContentType() == null || !archivo.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un documento PDF.");
        }

        // 4. Validar el tamaño máximo de 20 MB (OP-08, RNF_009)
        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 20 MB.");
        }

        // 5. Preparar la carpeta de almacenamiento en el sistema local
        String directorioActual = System.getProperty("user.dir");
        Path rutaAlmacenamiento = Paths.get(directorioActual + DIRECTORIO_STORAGE);
        if (!Files.exists(rutaAlmacenamiento)) {
            Files.createDirectories(rutaAlmacenamiento);
        }

        // 6. Guardar el archivo físicamente con un timestamp para evitar sobrescribir archivos con el mismo nombre
        String nombreOriginal = archivo.getOriginalFilename();
        String nombreSeguro = System.currentTimeMillis() + "_" + nombreOriginal;
        Path rutaDestino = rutaAlmacenamiento.resolve(nombreSeguro);
        archivo.transferTo(rutaDestino.toFile());

        // 7. Construir y guardar la entidad en la base de datos
        // Nota: La fechaEntrega se registra automáticamente gracias a @PrePersist en el modelo
        EntregaAvance entrega = EntregaAvance.builder()
                .tesis(tesis)
                .nombreArchivo(nombreOriginal)
                .rutaArchivo(DIRECTORIO_STORAGE + nombreSeguro)
                .tamanioBytes(archivo.getSize())
                .comentario(comentario != null && !comentario.trim().isEmpty() ? comentario.trim() : null)
                .build();

        EntregaAvance entregaGuardada = entregaAvanceRepository.save(entrega);

        // 8. Registrar el evento en el historial de la tesis (OP-05, RNF_015)
        historialCambioService.registrarCambio(
                tesis,
                tesis.getTesista(),
                TipoCambio.ENTREGA_AVANCE,
                "Se registró una nueva entrega de avance: " + nombreOriginal
        );

        // 9. Notificar al profesor guía por correo (OP-10, RF-019)
        notificacionService.notificarNuevaEntrega(entregaGuardada);

        return entregaGuardada;
    }

    public List<EntregaAvance> obtenerEntregasPorTesis(Long tesisId) {
        // Aprovechamos el metodo personalizado creado por la Persona 1
        return entregaAvanceRepository.findByTesisIdOrderByFechaEntregaDesc(tesisId);
    }

    // Busca una entrega puntual y valida que efectivamente pertenezca a la tesis indicada
    // en la URL (evita que se pueda acceder a un archivo de otra tesis adivinando el id).
    public EntregaAvance obtenerEntregaDeTesis(Long tesisId, Long entregaId) {
        EntregaAvance entrega = entregaAvanceRepository.findById(entregaId)
                .orElseThrow(() -> new IllegalArgumentException("La entrega con ID " + entregaId + " no existe."));

        if (!entrega.getTesis().getId().equals(tesisId)) {
            throw new IllegalArgumentException("La entrega con ID " + entregaId + " no pertenece a la tesis " + tesisId + ".");
        }

        return entrega;
    }

    // Carga el archivo físico de una entrega para poder visualizarlo/descargarlo.
    public Resource cargarArchivo(EntregaAvance entrega) throws MalformedURLException {
        String directorioActual = System.getProperty("user.dir");

        // Se reconstruye la ruta igual que en registrarEntrega (directorioActual + rutaArchivo)
        // y se normaliza/valida contra la carpeta raíz de almacenamiento para evitar que una
        // rutaArchivo corrupta o manipulada permita escapar del directorio de entregas.
        Path raiz = Paths.get(directorioActual + DIRECTORIO_STORAGE).normalize();
        Path ruta = Paths.get(directorioActual + entrega.getRutaArchivo()).normalize();

        if (!ruta.startsWith(raiz)) {
            throw new IllegalStateException("Ruta de archivo inválida.");
        }

        if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
            throw new IllegalArgumentException("El archivo de esta entrega ya no está disponible en el servidor.");
        }

        Resource recurso = new UrlResource(ruta.toUri());
        return recurso;
    }
}