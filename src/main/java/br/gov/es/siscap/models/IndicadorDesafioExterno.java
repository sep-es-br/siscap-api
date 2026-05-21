package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "indicador_desafio_externo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorDesafioExterno extends ControleHistorico {
 
    @Id
    @Column(name = "id_desafio")
    private Integer id;

    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gestao")
    private IndicadorGestaoExterno gestao;


}
