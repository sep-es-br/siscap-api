package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
 
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
 
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
 
    @Column(name = "apagado")
    private Boolean apagado;
 
    @OneToMany(mappedBy = "gestao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndicadorDesafioExterno> desafios;
 
    @OneToMany(mappedBy = "gestao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndicadorOrganizadorExterno> organizadores;
    
}
