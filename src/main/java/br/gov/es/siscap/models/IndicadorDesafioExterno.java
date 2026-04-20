package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "indicador_desafio_externo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorDesafioExterno extends ControleHistorico {
 
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
 
    @Column(name = "nome", nullable = false, length = 500)
    private String nome;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestao_id", nullable = false)
    private IndicadorGestaoExterno gestao;
 
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
 
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
 
    @Column(name = "apagado")
    private Boolean apagado;

}
