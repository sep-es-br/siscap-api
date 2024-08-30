package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusEnum;
import br.gov.es.siscap.form.ProgramaForm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

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
	@JoinColumn(name = "id_status", nullable = false)
	private Status status;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "orgao_executor", nullable = false)
	private Organizacao orgaoExecutor;

	@OneToMany(mappedBy = "programa", fetch = FetchType.LAZY)
	private Set<ProgramaPessoa> programaPessoaSet;

	@OneToMany(mappedBy = "programa", fetch = FetchType.LAZY)
	private Set<ProgramaProjeto> programaProjetoSet;

	@OneToMany(mappedBy = "programa", fetch = FetchType.LAZY)
	private Set<ProgramaValor> programaValorSet;

	public Programa(ProgramaForm form) {
		super();
		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());
		this.setOrgaoExecutor(new Organizacao(form.idOrgaoExecutor()));
		this.setStatus(new Status(StatusEnum.ATIVO.getValue()));
	}

	public void atualizar(ProgramaForm form) {
		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());
		this.setOrgaoExecutor(new Organizacao(form.idOrgaoExecutor()));
		super.setAtualizadoEm(LocalDateTime.now());
	}

	public void apagar() {
		this.setSigla(null);
		super.apagarHistorico();
	}
}