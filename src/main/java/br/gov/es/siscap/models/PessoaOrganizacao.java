package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "pessoa_organizacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update pessoa_organizacao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class PessoaOrganizacao {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_organizacao_id_gen")
	@SequenceGenerator(name = "pessoa_organizacao_id_gen", sequenceName = "pessoa_organizacao_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_organizacao", nullable = false)
	private Organizacao organizacao;

	@Column(name = "responsavel")
	private Boolean responsavel = Boolean.FALSE;

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
	private boolean apagado = Boolean.FALSE;

	public PessoaOrganizacao(Pessoa pessoa, Organizacao organizacao) {
		this.setPessoa(pessoa);
		this.setOrganizacao(organizacao);
	}

	public void apagar() {
		this.setDataFim(LocalDateTime.now());
		this.setAtualizadoEm(LocalDateTime.now());
		this.setApagado(true);
	}
}