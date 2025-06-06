package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.enums.TipoEquipeEnum;
import br.gov.es.siscap.enums.TipoPapelEnum;
import br.gov.es.siscap.enums.TipoStatusEnum;
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
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class ProjetoPessoa extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_pessoa_id_gen")
	@SequenceGenerator(name = "projeto_pessoa_id_gen", sequenceName = "projeto_pessoa_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@ManyToOne()
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	@NotNull
	@ManyToOne()
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@OneToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "id_tipo_papel")
	private TipoPapel tipoPapel;

	@OneToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "id_tipo_equipe")
	private TipoEquipe tipoEquipe;

	@ManyToOne()
	@JoinColumn(name = "id_tipo_status")
	private TipoStatus tipoStatus;

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
		this.setTipoPapel(new TipoPapel(TipoPapelEnum.RESPONSAVEL_PROPONENTE.getValue()));
		this.setTipoEquipe(new TipoEquipe(TipoEquipeEnum.ELABORACAO.getValue()));
		this.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
		this.setJustificativa(null);
	}

	public ProjetoPessoa(Projeto projeto, EquipeDto equipeDto) {
		this.setProjeto(projeto);
		this.setPessoa(new Pessoa(equipeDto.idPessoa()));
		this.setTipoPapel(new TipoPapel(equipeDto.idPapel()));
		this.setTipoEquipe(new TipoEquipe(TipoEquipeEnum.ELABORACAO.getValue()));
		this.setTipoStatus(new TipoStatus(equipeDto.idStatus()));
		this.setJustificativa(equipeDto.justificativa());
	}

	public void atualizarResponsavelProponente(Long idStatus) {
		this.setTipoStatus(new TipoStatus(idStatus));
		this.setDataFim(LocalDateTime.now());
		super.atualizarHistorico();
	}

	public void atualizarMembroEquipe(EquipeDto equipeDto) {
		this.setTipoPapel(new TipoPapel(equipeDto.idPapel()));
		if (!Objects.equals(equipeDto.idStatus(), TipoStatusEnum.ATIVO.getValue())) {
			this.setTipoStatus(new TipoStatus(equipeDto.idStatus()));
			this.setJustificativa(equipeDto.justificativa());
			this.setDataFim(LocalDateTime.now());
			super.atualizarHistorico();
		}
	}

	public void apagar(String justificativa) {
		this.setTipoStatus(new TipoStatus(TipoStatusEnum.EXCLUIDO.getValue()));
		this.setJustificativa(justificativa);
		this.setDataFim(LocalDateTime.now());
		super.atualizarHistorico();
	}

	public boolean isResponsavelProponente() {
		return this.getTipoPapel() != null &&
			   Objects.equals(this.getTipoPapel().getId(), TipoPapelEnum.RESPONSAVEL_PROPONENTE.getValue());
	}
	

	public boolean compararIdPessoaComEquipeDto(EquipeDto equipeDto) {
		return Objects.equals(this.getPessoa().getId(), equipeDto.idPessoa());
	}
}