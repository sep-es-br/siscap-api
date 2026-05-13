package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;

@Entity
@Table(name = "indicador_avulso_meta")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_avulso_meta set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorAvulsoMeta extends ControleHistorico {

	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "indicador_avulso_meta_id_gen"
    )
    @SequenceGenerator(
        name = "indicador_avulso_meta_id_gen",
        sequenceName = "indicador_avulso_meta_id_seq",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_indicador", nullable = false)
    private IndicadorAvulso indicadorAvulso;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "valor", precision = 19, scale = 2)
    private String valor;

    public IndicadorAvulsoMeta(
        ProjetoIndicadorAvulsoMetaDto metaDto
    ) {
        this.ano = metaDto.anoMeta();
        this.valor = metaDto.valorMeta();
    }

}