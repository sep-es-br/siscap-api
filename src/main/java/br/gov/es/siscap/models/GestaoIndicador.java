package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "gestao_indicador")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update gestao_indicador set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class GestaoIndicador extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gestao_indicador_id_gen")
	@SequenceGenerator(name = "gestao_indicador_id_gen", sequenceName = "gestao_indicador_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "periodo_gestao", nullable = false)
	private Integer periodo;

	@Column(name = "tipo_periodo_gestao", nullable = false)
	private Integer tipo_periodo;

	@Column(name = "ano_inicio_gestao", nullable = false)
	private Integer ano_inicio;

	@ManyToOne()
	@JoinColumn(name = "id_tipo_status")
	private TipoStatus tipoStatus;

}