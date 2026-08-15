package grupo3.fingeso.service;

import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.repository.TesisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    // RNF_021: un tesista no puede ver datos de otro tesista. Los profesores, por ahora,
    // conservan visibilidad amplia (CU-05 no restringe a solo sus propios guiados).
    public boolean puedeVerTesis(Tesis tesis, Authentication authentication) {
        if (!esTesista(authentication)) {
            return true;
        }
        String email = authentication.getName();
        return tesis.getTesista() != null && email.equalsIgnoreCase(tesis.getTesista().getEmail());
    }

    public List<Tesis> filtrarVisiblesParaUsuario(List<Tesis> tesis, Authentication authentication) {
        if (!esTesista(authentication)) {
            return tesis;
        }
        String email = authentication.getName();
        return tesis.stream()
                .filter(t -> t.getTesista() != null && email.equalsIgnoreCase(t.getTesista().getEmail()))
                .toList();
    }

    private boolean esTesista(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_TESISTA".equals(a.getAuthority()));
    }
}
