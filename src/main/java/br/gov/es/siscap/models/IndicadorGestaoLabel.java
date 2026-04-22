package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_gestao_label")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_gestao_label set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class IndicadorGestaoLabel extends ControleHistorico {
 
    @EmbeddedId
    private GestaoLabelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idGestao")
    @JoinColumn(name = "id_gestao")
    private IndicadorFatoExterno gestao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idLabel")
    @JoinColumn(name = "id_label")
    private IndicadorLabel label;

    private Integer ordem;
    
    
}
