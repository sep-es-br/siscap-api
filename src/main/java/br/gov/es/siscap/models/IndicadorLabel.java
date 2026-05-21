package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_label")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_label set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorLabel extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_label")
    private Integer id;

    private String nome;

    @OneToMany(mappedBy = "label", fetch = FetchType.LAZY)
    private Set<IndicadorLabelValor> valores;

}
