package grupo3.fingeso.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Especialización de {@link Usuario} — estudiante que desarrolla una tesis.
 */
@Entity
@DiscriminatorValue("TESISTA")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Tesista extends Usuario {

    private String carrera;
}
