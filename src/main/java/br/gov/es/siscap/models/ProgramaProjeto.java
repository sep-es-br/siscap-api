package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.ProjetoPropostoDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "programa_projeto")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update programa_projeto set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProgramaProjeto extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_projeto_id_gen")
	@SequenceGenerator(name = "programa_projeto_id_gen", sequenceName = "programa_projeto_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_programa", nullable = false)
	private Programa programa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@Column(name = "valor", nullable = false, precision = 25, scale = 2)
	private BigDecimal valor;

	public ProgramaProjeto(Programa programa, ProjetoPropostoDto projetoPropostoDto) {
		super();
		this.setPrograma(programa);
		this.setProjeto(new Projeto(projetoPropostoDto.idProjeto()));
		this.setValor(projetoPropostoDto.valor());
	}

	public void atualizarProjetoProposto(ProjetoPropostoDto projetoPropostoDto) {

	}

	public void apagarProjetoProposto() {
		super.apagarHistorico();
	}

	public boolean compararIdProjetoComProjetoPropostoDto(ProjetoPropostoDto projetoPropostoDto) {
		return Objects.equals(this.getProjeto().getId(), projetoPropostoDto.idProjeto());
	}

}