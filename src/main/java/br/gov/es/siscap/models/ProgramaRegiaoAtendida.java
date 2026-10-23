package br.gov.es.siscap.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Guarda a identificação e o nome da região no momento em que o Programa é salvo. */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaRegiaoAtendida {
    @Column(name = "id_localidade", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;
}
