package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "indicador_catalogo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_catalogo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorCatalogo extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_catalogo_id_gen")
	@SequenceGenerator(name = "indicador_catalogo_id_gen", sequenceName = "indicador_catalogo_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_gestao_indicador", nullable = false)
	private GestaoIndicador gestaoIndicador;

	@Column(name = "nome", length = 255, nullable = false)
	private String nome;

	@Column(name = "formula", columnDefinition = "TEXT")
	private String formula;

	@Column(name = "fonte", length = 255)
	private String fonte;

	@Column(name = "unidade_medida", length = 50)
	private String unidadeMedida;

	@Column(name = "base_referencia_valor", precision = 10, scale = 2)
	private String baseReferenciaValor;

	@Column(name = "fonte_origem", length = 30, nullable = false)
    private String fonteOrigem; // EXTERNO, MANUAL, LEGADO

	// @Column(name = "base_referencia_ano")
	// private Integer baseReferenciaAno;

	// @Column(name = "polaridade", length = 20)
	// @Builder.Default
	// private String polaridade = "negativa"; // negativa (quanto menor, melhor) ou positiva

	// @Column(name = "observacoes", columnDefinition = "TEXT")
	// private String observacoes;

	// @Column(name = "justificativa_base", columnDefinition = "TEXT")
	// private String justificativaBase;

	@ManyToOne()
	@JoinColumn(name = "id_tipo_status")
	private TipoStatus tipoStatus;

}