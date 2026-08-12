package grupo3.fingeso.repository;

import grupo3.fingeso.model.EntregaAvance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntregaAvanceRepository extends JpaRepository<EntregaAvance, Long> {

    List<EntregaAvance> findByTesisIdOrderByFechaEntregaDesc(Long tesisId);
}
