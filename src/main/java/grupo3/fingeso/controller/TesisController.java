package grupo3.fingeso.controller;

import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.service.TesisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tesis")
@RequiredArgsConstructor
public class TesisController {

    private final TesisService tesisService;

    // GET /api/tesis
    // GET /api/tesis?estado=EN_DESARROLLO
    // GET /api/tesis?tesistaId=5
    @GetMapping
    public ResponseEntity<List<Tesis>> listarTesis(
            @RequestParam(required = false) EstadoTesis estado,
            @RequestParam(required = false) Long tesistaId,
            Authentication authentication) {

        List<Tesis> resultado;
        if (estado != null) {
            resultado = tesisService.obtenerTesisPorEstado(estado);
        } else if (tesistaId != null) {
            resultado = tesisService.obtenerTesisPorTesista(tesistaId);
        } else {
            resultado = tesisService.obtenerTodasLasTesis();
        }

        // RNF_021: un tesista solo puede ver sus propias tesis, sin importar qué
        // filtros haya pasado en la query — no basta con que el front oculte el resto.
        return ResponseEntity.ok(tesisService.filtrarVisiblesParaUsuario(resultado, authentication));
    }

    // GET /api/tesis/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTesisPorId(@PathVariable Long id, Authentication authentication) {
        return tesisService.obtenerTesisPorId(id)
                .map(tesis -> {
                    if (!tesisService.puedeVerTesis(tesis, authentication)) {
                        // RNF_003 / RNF_021: acceso no autorizado a la tesis de otro tesista -> 403
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body((Object) "No tienes acceso a esta tesis.");
                    }
                    return ResponseEntity.ok((Object) tesis);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}