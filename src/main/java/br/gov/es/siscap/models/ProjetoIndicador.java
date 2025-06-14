package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.enums.TipoStatusEnum;


@Entity
@Table(name = "projeto_indicador")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_indicador set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class ProjetoIndicador extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_indicador_id_gen")
	@SequenceGenerator(name = "projeto_indicador_id_gen", sequenceName = "projeto_indicador_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;
	
	@Column(name = "tipo_indicador", nullable = false)
	private String tipoIndicador;

	@Column(name = "descricao_indicador", nullable = false, length = 2000)
	private String descricaoIndicador;

	@Column(name = "meta_indicador", nullable = false, length = 2000)
	private String descricaoMeta;

	@ManyToOne()
	@JoinColumn(name = "id_tipo_status")
	private TipoStatus tipoStatus;

	public ProjetoIndicador(Projeto projeto, ProjetoIndicadorDto indicador) {
		this.setProjeto(projeto);
		this.setId(indicador.idIndicador());
		this.setTipoIndicador(indicador.tipoIndicador());
		this.setDescricaoIndicador(indicador.descricaoIndicador());
		this.setDescricaoMeta(indicador.descricaoMeta());
		this.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
	}

}