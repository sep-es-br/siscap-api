package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.enums.EquipeEnum;
import br.gov.es.siscap.enums.PapelEnum;
import br.gov.es.siscap.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "projeto_pessoa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_pessoa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE and id_status = 1")
public class ProjetoPessoa extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_pessoa_id_gen")
	@SequenceGenerator(name = "projeto_pessoa_id_gen", sequenceName = "projeto_pessoa_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@OneToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "id_papel")
	private Papel papel;

	@OneToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "id_equipe")
	private Equipe equipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_status")
	private Status status;

	@Column(name = "justificativa")
	private String justificativa;

	@DateTimeFormat
	@Column(name = "data_inicio")
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;


	public ProjetoPessoa(Projeto projeto, Long idResponsavelProponente) {
		this.setProjeto(projeto);
		this.setPessoa(new Pessoa(idResponsavelProponente));
		this.setPapel(new Papel(PapelEnum.RESPONSAVEL_PROPONENTE.getValue()));
		this.setEquipe(new Equipe(EquipeEnum.ELABORACAO.getValue()));
		this.setStatus(new Status(StatusEnum.ATIVO.getValue()));
		this.setJustificativa(null);
	}

	public ProjetoPessoa(Projeto projeto, EquipeDto equipeDto) {
		this.setProjeto(projeto);
		this.setPessoa(new Pessoa(equipeDto.idPessoa()));
		this.setPapel(new Papel(equipeDto.idPapel()));
		this.setEquipe(new Equipe(EquipeEnum.ELABORACAO.getValue()));
		this.setStatus(new Status(equipeDto.idStatus()));
		this.setJustificativa(equipeDto.justificativa());
	}

	public void atualizarResponsavelProponente(Long idStatus) {
		this.setStatus(new Status(idStatus));
		this.setDataFim(LocalDateTime.now());
		super.atualizarHistorico();
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
		super.atualizarHistorico();
	}

	public boolean isResponsavelProponente() {
		return Objects.equals(this.getPapel().getId(), PapelEnum.RESPONSAVEL_PROPONENTE.getValue());
	}

	public boolean compararIdPessoaComEquipeDto(EquipeDto equipeDto) {
		return Objects.equals(this.getPessoa().getId(), equipeDto.idPessoa());
	}
}