package grupo3.fingeso.service;

import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.repository.TesisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TesisService {

    private final TesisRepository tesisRepository;

    public List<Tesis> obtenerTodasLasTesis() {
        return tesisRepository.findAll();
    }

    public Optional<Tesis> obtenerTesisPorId(Long id) {
        return tesisRepository.findById(id);
    }

    public List<Tesis> obtenerTesisPorTesista(Long tesistaId) {
        return tesisRepository.findByTesistaId(tesistaId);
    }

    public List<Tesis> obtenerTesisPorEstado(EstadoTesis estado) {
        return tesisRepository.findByEstado(estado);
    }
}
