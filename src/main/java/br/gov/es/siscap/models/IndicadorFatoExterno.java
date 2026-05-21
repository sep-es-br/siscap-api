package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_fato_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_fato_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorFatoExterno extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fato")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gestao")
    private IndicadorGestaoExterno gestao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_desafio")
    private IndicadorDesafioExterno desafio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizador")
    private IndicadorOrganizadorExterno organizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_indicador")
    private IndicadorExterno indicador;

    private Integer ano;

    @Column(name = "valor_meta")
    private BigDecimal valorMeta;

    @Column(name = "maior_ano_indicador")
    private Integer maiorAnoIndicador;

    @Column(name = "maior_meta_indicador")
    private BigDecimal maiorMetaIndicador;
 
    @Column(name = "dt_importacao", nullable = false)
    private LocalDateTime dtImportacao;

}
