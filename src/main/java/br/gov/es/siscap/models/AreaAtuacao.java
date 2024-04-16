package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "area_atuacao")
@Getter
@NoArgsConstructor
public class AreaAtuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nome;

    public AreaAtuacao(String id) {
        this.id = id;
    }

}
