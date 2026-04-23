package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorExterno extends ControleHistorico {

	@Id
    @Column(name = "id_indicador")
    private Integer id;

    private String nome;

    @Column(name = "unidade_medida")
    private String unidadeMedida;

    private String polaridade;

    @Column(name = "medido_por")
    private String medidoPor;
    
}