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
@Table(name = "indicador_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorFatoExterno extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicador_id", nullable = false)
    private IndicadorExterno indicador;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestao_id", nullable = false)
    private IndicadorGestaoExterno gestao;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desafio_id", nullable = false)
    private IndicadorDesafioExterno desafio;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id", nullable = false)
    private IndicadorOrganizadorExterno organizador;
 
    @Column(name = "ano", nullable = false)
    private Integer ano;
 
    @Column(name = "meta", precision = 18, scale = 4)
    private BigDecimal meta;
 
    @Column(name = "maior_ano")
    private Integer maiorAno;
 
    @Column(name = "maior_meta", precision = 18, scale = 4)
    private BigDecimal maiorMeta;
 
    @Column(name = "dt_importacao", nullable = false)
    private LocalDateTime dtImportacao;

}
