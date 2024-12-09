package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.form.ProgramaForm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "programa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update programa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Programa extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_id_gen")
	@SequenceGenerator(name = "programa_id_gen", sequenceName = "programa_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 12)
	@Column(name = "sigla", length = 12)
	private String sigla;

	@Size(max = 150)
	@NotNull
	@Column(name = "titulo", nullable = false, length = 150)
	private String titulo;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_status", nullable = false)
	private TipoStatus tipoStatus;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "programa_organizacao",
				joinColumns = {@JoinColumn(name = "id_programa", nullable = false)},
				inverseJoinColumns = @JoinColumn(name = "id_organizacao", nullable = false))
	private Set<Organizacao> orgaoExecutorSet;

	@OneToMany(mappedBy = "programa", fetch = FetchType.LAZY)
	private Set<ProgramaPessoa> programaPessoaSet;

	@NotNull
	@DateTimeFormat
	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio = LocalDateTime.now();

	@DateTimeFormat
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

	@Column(name = "teto_quantia", scale = 25, precision = 2)
	private BigDecimal tetoQuantia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_valor")
	private TipoValor tipoValor;

	@Size(max = 3)
	@Column(name = "moeda")
	private String moeda;

	public Programa(Long id) {
		this.setId(id);
	}

	public Programa(ProgramaForm form) {
		this.setDadosPrograma(form);
		this.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
	}

	public void atualizar(ProgramaForm form) {
		this.setDadosPrograma(form);
		super.atualizarHistorico();
	}

	public void apagar() {
		this.setSigla(null);
		this.setDataFim(LocalDateTime.now());
		super.apagarHistorico();
	}

	private void setDadosPrograma(ProgramaForm form) {
		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());
		this.setOrgaoExecutorSet(
					form.idOrgaoExecutorList()
								.stream()
								.map(Organizacao::new)
								.collect(Collectors.toSet())
		);
		this.setTetoQuantia(form.valor().quantia());
		this.setTipoValor(new TipoValor(form.valor().tipo()));
		this.setMoeda(form.valor().moeda());
	}
}