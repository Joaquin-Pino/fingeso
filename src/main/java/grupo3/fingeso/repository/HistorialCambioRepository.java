package grupo3.fingeso.repository;

import grupo3.fingeso.model.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Long> {

    List<HistorialCambio> findByTesisIdOrderByFechaDesc(Long tesisId);
}
