package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.EquipeDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projeto_pessoa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_pessoa set apagado = true, data_fim = current_timestamp, atualizado_em = current_timestamp, justificativa = 'CASCADE_DELETE', id_status = 3 where id=?")
@SQLRestriction("apagado = FALSE and id_status = 1")
public class ProjetoPessoa {

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

	@DateTimeFormat
	@Column(name = "data_inicio")
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private Boolean apagado = Boolean.FALSE;

	@Column(name = "justificativa")
	private String justificativa;

	public ProjetoPessoa(Projeto projeto, Long idResponsavelProponente) {
		this.setProjeto(projeto);
		this.setPessoa(new Pessoa(idResponsavelProponente));
		this.setPapel(new Papel(2L));
		this.setEquipe(new Equipe(1L));
		this.setStatus(new Status(1L));
		this.setJustificativa(null);
	}

	public ProjetoPessoa(Projeto projeto, EquipeDto equipeDto) {
		this.setProjeto(projeto);
		this.setPessoa(new Pessoa(equipeDto.idPessoa()));
		this.setPapel(new Papel(equipeDto.idPapel()));
		this.setEquipe(new Equipe(1L));
		this.setStatus(new Status(equipeDto.idStatus()));
		this.setJustificativa(equipeDto.justificativa());
	}

	public void atualizar() {
		this.setStatus(new Status(2L));
		this.setDataFim(LocalDateTime.now());
		this.setAtualizadoEm(LocalDateTime.now());
	}

	public void excluirMembro(EquipeDto equipeDto) {
		this.setPapel(new Papel(equipeDto.idPapel()));
		this.setStatus(new Status(equipeDto.idStatus()));
		this.setJustificativa(equipeDto.justificativa());
		this.setDataFim(LocalDateTime.now());
		this.setAtualizadoEm(LocalDateTime.now());
		this.setApagado(true);
	}
}