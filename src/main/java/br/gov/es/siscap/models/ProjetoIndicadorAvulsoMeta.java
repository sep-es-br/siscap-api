package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;

@Entity
@Table(name = "projeto_indicador_avulso_meta")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_indicador_avulso_meta set apagado = true where id=?")
@SQLRestriction("apagado = false")
public class ProjetoIndicadorAvulsoMeta extends ControleHistorico {

	public ProjetoIndicadorAvulsoMeta(ProjetoIndicadorAvulsoMetaDto dto) {
		this.setAnoMeta(dto.anoMeta());
		this.setValorMeta(dto.valorMeta());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_indicador_avulso_meta_id_seq")
	@SequenceGenerator(name = "projeto_indicador_avulso_meta_id_seq", sequenceName = "projeto_indicador_avulso_meta_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "ano_meta", nullable = false)
	private Integer anoMeta;

	@Column(name = "valor_meta", nullable = false)
	private String valorMeta;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto_indicador_avulso", nullable = false)
	private ProjetoIndicadorAvulso projetoIndicadorAvulso;

}
