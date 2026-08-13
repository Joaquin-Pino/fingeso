package grupo3.fingeso.service;

import grupo3.fingeso.model.HistorialCambio;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.model.TipoCambio;
import grupo3.fingeso.model.Usuario;
import grupo3.fingeso.repository.HistorialCambioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Escribe y lee la bitácora de cambios de una tesis (OP-05).
 * Cada registro es inmutable una vez guardado: no existe un método "editar"
 * ni "borrar" a propósito (RNF_015 — trazabilidad histórica).
 */
@Service
@RequiredArgsConstructor
public class HistorialCambioService {

    private final HistorialCambioRepository historialCambioRepository;

    /**
     * Crea una nueva línea en la bitácora de la tesis indicada.
     *
     * @param tesis        la tesis afectada
     * @param usuario      quién provocó el cambio (ej. el tesista que subió la entrega)
     * @param tipoCambio   qué tipo de evento fue
     * @param descripcion  texto legible para mostrar en el historial
     */
    public HistorialCambio registrarCambio(Tesis tesis, Usuario usuario, TipoCambio tipoCambio, String descripcion) {
        HistorialCambio cambio = HistorialCambio.builder()
                .tesis(tesis)
                .usuario(usuario)
                .tipoCambio(tipoCambio)
                .descripcion(descripcion)
                .build();

        return historialCambioRepository.save(cambio);
    }

    /** Devuelve la bitácora completa de una tesis, más reciente primero. */
    public List<HistorialCambio> obtenerHistorialPorTesis(Long tesisId) {
        return historialCambioRepository.findByTesisIdOrderByFechaDesc(tesisId);
    }
}