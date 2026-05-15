package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_avulso")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_avulso set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorAvulso extends ControleHistorico {

	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "indicador_avulso_id_gen"
    )
    @SequenceGenerator(
        name = "indicador_avulso_id_gen",
        sequenceName = "indicador_avulso_id_seq",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "base_de_referencia", length = 2000)
    private String baseDeReferencia;

    @Column(name = "fonte_indicador", length = 2000)
    private String fonteIndicador;

    @Column(name = "nome_indicador", length = 2000)
    private String nomeIndicador;

    @Column(name = "unidade_medida", length = 2000)
    private String unidadeMedida;

    @Column(name = "medido_por", length = 2000)
    private String medidoPor;

    @OneToMany( mappedBy = "indicadorAvulso", cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<IndicadorAvulsoMeta> metasIndicadorAvulso = new HashSet<>();

    public IndicadorAvulso(ProjetoIndicadorAvulsoDto indicador) {

        this.id = indicador.indicadorAvulso().id();

        this.baseDeReferencia = indicador.indicadorAvulso().baseDeReferencia();
        this.fonteIndicador = indicador.indicadorAvulso().fonteIndicador();
        this.nomeIndicador = indicador.indicadorAvulso().nomeIndicador();
        this.unidadeMedida = indicador.indicadorAvulso().unidadeMedida();
        this.medidoPor = indicador.indicadorAvulso().medidoPor();

        if (indicador.indicadorAvulso().metasGlobais() != null) {
            indicador.indicadorAvulso().metasGlobais().forEach(metaDto -> {
                IndicadorAvulsoMeta meta = new IndicadorAvulsoMeta(metaDto);
                this.addMetaGlobal(meta);
            });
        }
		
    }

    public void addMetaGlobal(IndicadorAvulsoMeta meta) {
        meta.setIndicadorAvulso(this);
        this.metasIndicadorAvulso.add(meta);
    }

}