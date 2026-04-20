package br.gov.es.siscap.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "indicador_organizador_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_organizador_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class IndicadorOrganizadorExterno extends ControleHistorico {
 
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
 
    @Column(name = "nome", nullable = false, length = 255)
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
