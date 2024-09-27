package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.enums.EquipeEnum;
import br.gov.es.siscap.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "programa_pessoa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update programa_pessoa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_status = 1")
public class ProgramaPessoa extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_pessoa_id_gen")
	@SequenceGenerator(name = "programa_pessoa_id_gen", sequenceName = "programa_pessoa_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_programa", nullable = false)
	private Programa programa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_papel", nullable = false)
	private Papel papel;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_equipe", nullable = false)
	private Equipe equipe;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_status", nullable = false)
	private Status status;

	@Size(max = 255)
	@Column(name = "justificativa")
	private String justificativa;

	@NotNull
	@DateTimeFormat
	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	public ProgramaPessoa(Programa programa, EquipeDto equipeDto) {
		this.setPrograma(programa);
		this.setPessoa(new Pessoa(equipeDto.idPessoa()));
		this.setPapel(new Papel(equipeDto.idPapel()));
		this.setEquipe(new Equipe(EquipeEnum.CAPTACAO.getValue()));
		this.setStatus(new Status(StatusEnum.ATIVO.getValue()));
	}

	public void atualizarMembroEquipe(EquipeDto equipeDto) {
		this.setPapel(new Papel(equipeDto.idPapel()));
		if (!Objects.equals(equipeDto.idStatus(), StatusEnum.ATIVO.getValue())) {
			this.setStatus(new Status(equipeDto.idStatus()));
			this.setJustificativa(equipeDto.justificativa());
			this.setDataFim(LocalDateTime.now());
			super.atualizarHistorico();
		}
	}

	public void apagar(String justificativa) {
		this.setStatus(new Status(StatusEnum.EXCLUIDO.getValue()));
		this.setJustificativa(justificativa);
		this.setDataFim(LocalDateTime.now());
		super.apagarHistorico();
	}

	public boolean compararIdPessoaComEquipeDto(EquipeDto equipeDto) {
		return Objects.equals(this.getPessoa().getId(), equipeDto.idPessoa());
	}
}