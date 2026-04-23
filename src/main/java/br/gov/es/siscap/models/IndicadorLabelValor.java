package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_label_valor",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_label", "valor"}))
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_label_valor set apagado = true where id_label_valor=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorLabelValor extends ControleHistorico {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_label_valor")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_label")
    private IndicadorLabel label;

    private String valor;
    
}
