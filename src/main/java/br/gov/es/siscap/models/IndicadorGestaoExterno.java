package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_gestao_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_gestao_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class IndicadorGestaoExterno extends ControleHistorico {
 
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
 
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;
 
    @Column(name = "ativa", nullable = false)
    private Boolean ativa;
 
    @Column(name = "model_label", length = 1000)
    private String modelLabel;
    
    @OneToMany(mappedBy = "gestao", fetch = FetchType.LAZY)
    private List<IndicadorGestaoLabel> labels;

}
