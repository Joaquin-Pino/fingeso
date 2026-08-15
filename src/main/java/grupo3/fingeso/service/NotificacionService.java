package grupo3.fingeso.service;

import grupo3.fingeso.model.EntregaAvance;
import grupo3.fingeso.model.Profesor;
import grupo3.fingeso.model.Tesis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envía notificaciones por correo (OP-10, RF-019).
 * Por ahora solo cubre el caso de CU-07: avisar al profesor guía cuando
 * hay una nueva entrega pendiente de revisión.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final JavaMailSender mailSender;

    public void notificarNuevaEntrega(EntregaAvance entrega) {
        Tesis tesis = entrega.getTesis();
        Profesor guia = tesis.getProfesorGuia();

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(guia.getEmail());
        mensaje.setSubject("Nueva entrega de avance — " + tesis.getTitulo());
        mensaje.setText(
                "Hola " + guia.getNombre() + ",\n\n" +
                        "El tesista " + tesis.getTesista().getNombre() +
                        " acaba de subir una nueva entrega de avance para la tesis \"" + tesis.getTitulo() + "\".\n\n" +
                        "Archivo: " + entrega.getNombreArchivo() + "\n" +
                        "Fecha de entrega: " + entrega.getFechaEntrega() + "\n\n" +
                        "Ingresa al sistema para revisarla.\n\n" +
                        "-- Sistema de Gestión de Tesis"
        );

        try {
            mailSender.send(mensaje);
            log.info("Notificación de nueva entrega enviada a {}", guia.getEmail());
        } catch (Exception e) {
            // Si el correo falla, no queremos que se pierda la entrega ya guardada en la BD.
            // Solo lo dejamos anotado en el log para revisarlo después.
            log.error("No se pudo enviar la notificación a {}: {}", guia.getEmail(), e.getMessage());
        }
    }
}