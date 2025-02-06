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
public class PessoaOrganizacao extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_organizacao_id_gen")
	@SequenceGenerator(name = "pessoa_organizacao_id_gen", sequenceName = "pessoa_organizacao_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_organizacao", nullable = false)
	private Organizacao organizacao;

	@Column(name = "responsavel")
	private Boolean isResponsavel = Boolean.FALSE;

	@NotNull
	@DateTimeFormat
	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	public PessoaOrganizacao(Pessoa pessoa, Organizacao organizacao) {
		this.setPessoa(pessoa);
		this.setOrganizacao(organizacao);
	}

	public void apagarPessoaOrganizacao() {
		this.setDataFim(LocalDateTime.now());
		super.apagarHistorico();
	}
}