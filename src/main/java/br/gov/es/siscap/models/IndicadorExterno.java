package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class IndicadorExterno extends ControleHistorico {

	@Id
    @Column(name = "id", nullable = false)
    private Long id;
 
    @Column(name = "nome", nullable = false, length = 500)
    private String nome;
 
    @Column(name = "unidade_medida", length = 100)
    private String unidadeMedida;
 
    @Column(name = "polaridade", length = 50)
    private String polaridade;
 
    @Column(name = "medido_por", length = 100)
    private String medidoPor;
 
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
 
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

	@ManyToOne()
	@JoinColumn(name = "id_tipo_status")
	private TipoStatus tipoStatus;

}