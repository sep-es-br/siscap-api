package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoIndicadorOdsDto;

@Entity
@Table(name = "projeto_indicador_ods")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_indicador_ods set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoIndicadorOds extends ControleHistorico {

	public ProjetoIndicadorOds(ProjetoIndicadorOdsDto odsDto) {
		if (odsDto.idOdsIndicadorExterno() != null) {
			this.setOdsIndicadorExterno(
					new OdsIndicadorExterno(odsDto.idOdsIndicadorExterno()));
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_indicador_ods_id_gen")
	@SequenceGenerator(name = "projeto_indicador_ods_id_gen", sequenceName = "projeto_indicador_ods_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto_indicador", nullable = false)
	private ProjetoIndicador projetoIndicador;

	@NotNull
	@ManyToOne()
	@JoinColumn(name = "id_ods_indicador_externo")
	private OdsIndicadorExterno odsIndicadorExterno;

}