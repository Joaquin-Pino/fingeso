package grupo3.fingeso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Unidad principal del dominio — vincula un tesista con su(s) profesor(es) guía.
 */
@Entity
@Table(name = "tesis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tesista_id", nullable = false)
    private Tesista tesista;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesor_guia_id", nullable = false)
    private Profesor profesorGuia;

    @ManyToOne
    @JoinColumn(name = "profesor_coguia_id")
    private Profesor profesorCoguia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTesis estado;

    private LocalDate fechaInicio;
}
