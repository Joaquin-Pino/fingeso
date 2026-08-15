package grupo3.fingeso.controller;

import grupo3.fingeso.model.EntregaAvance;
import grupo3.fingeso.service.EntregaAvanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tesis")
@RequiredArgsConstructor
public class EntregaAvanceController {

    private final EntregaAvanceService entregaAvanceService;

    // POST /api/tesis/{id}/entregas
    @PostMapping("/{id}/entregas")
    public ResponseEntity<?> subirAvance(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            EntregaAvance entregaGuardada = entregaAvanceService.registrarEntrega(id, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(entregaGuardada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Manejamos los errores de validación (PDF, Tamaño o Estado de tesis) devolviendo un error 400
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejamos errores de servidor (ej. problemas de permisos al guardar el archivo) devolviendo un error 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar el archivo: " + e.getMessage());
        }
    }

    // GET /api/tesis/{id}/entregas
    @GetMapping("/{id}/entregas")
    public ResponseEntity<List<EntregaAvance>> listarAvances(@PathVariable Long id) {
        List<EntregaAvance> entregas = entregaAvanceService.obtenerEntregasPorTesis(id);
        return ResponseEntity.ok(entregas);
    }
}