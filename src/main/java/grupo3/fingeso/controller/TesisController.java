package grupo3.fingeso.controller;

import grupo3.fingeso.model.EstadoTesis;
import grupo3.fingeso.model.Tesis;
import grupo3.fingeso.service.TesisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) Long tesistaId) {

        if (estado != null) {
            return ResponseEntity.ok(tesisService.obtenerTesisPorEstado(estado));
        }
        if (tesistaId != null) {
            return ResponseEntity.ok(tesisService.obtenerTesisPorTesista(tesistaId));
        }
        return ResponseEntity.ok(tesisService.obtenerTodasLasTesis());
    }

    // GET /api/tesis/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Tesis> obtenerTesisPorId(@PathVariable Long id) {
        return tesisService.obtenerTesisPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}