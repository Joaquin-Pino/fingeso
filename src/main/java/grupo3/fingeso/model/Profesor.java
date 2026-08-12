package grupo3.fingeso.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Especialización de {@link Usuario} — guía, co-guía o miembro de comisión.
 */
@Entity
@DiscriminatorValue("PROFESOR")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Profesor extends Usuario {

    private String departamento;
}
