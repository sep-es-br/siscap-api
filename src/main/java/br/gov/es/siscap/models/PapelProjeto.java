package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "papel_projeto")
@Getter
@NoArgsConstructor
public class PapelProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nome;

    public PapelProjeto(UUID uuid) {
        this.id = uuid;
    }
}
