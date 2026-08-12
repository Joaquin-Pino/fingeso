package grupo3.fingeso.repository;

import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TesisRepository extends JpaRepository<Tesis, Long> {

    List<Tesis> findByTesistaId(Long tesistaId);

    List<Tesis> findByEstado(EstadoTesis estado);
}
