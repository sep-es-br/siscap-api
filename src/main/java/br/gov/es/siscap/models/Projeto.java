package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusEnum;
import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "projeto")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Projeto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "sigla")
	private String sigla;

	@Column(name = "titulo")
	private String titulo;

	@Column(name = "valor_estimado")
	private BigDecimal valorEstimado;

	@Column(name = "objetivo")
	private String objetivo;

	@Column(name = "objetivo_especifico")
	private String objetivoEspecifico;

	@ManyToOne
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "status")
	private Status status;

	@ManyToOne
	@JoinColumn(name = "id_organizacao")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacao;

	@Column(name = "situacao_problema")
	private String situacaoProblema;

	@Column(name = "solucoes_propostas")
	private String solucoesPropostas;

	@Column(name = "impactos")
	private String impactos;

	@Column(name = "arranjos_institucionais")
	private String arranjosInstitucionais;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoCidade> projetoCidadeSet;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoPessoa> projetoPessoaSet;

	@OneToMany(mappedBy = "projeto")
	private Set<ProgramaProjeto> programaProjetoSet;

	@ManyToOne
	@JoinColumn(name = "id_area")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Area area;

	@DateTimeFormat
	@Column(name = "criado_em")
	private LocalDateTime criadoEm;

	@DateTimeFormat
	@Column(name = "atualizado_em")
	private LocalDateTime atualizadoEm;

	@Column(name = "apagado")
	private boolean apagado;

	public Projeto(Long id) {
		this.id = id;
	}

	public Projeto(ProjetoForm form) {
		this.sigla = form.sigla();
		this.titulo = form.titulo();
		this.valorEstimado = form.valorEstimado();
		this.objetivo = form.objetivo();
		this.objetivoEspecifico = form.objetivoEspecifico();
		this.status = new Status(1L);
		this.organizacao = new Organizacao(form.idOrganizacao());
		this.situacaoProblema = form.situacaoProblema();
		this.solucoesPropostas = form.solucoesPropostas();
		this.impactos = form.impactos();
		this.arranjosInstitucionais = form.arranjosInstitucionais();
		this.criadoEm = LocalDateTime.now();
		this.apagado = Boolean.FALSE;
	}

	public void atualizarProjeto(ProjetoForm form) {
		this.sigla = form.sigla();
		this.titulo = form.titulo();
		this.organizacao = new Organizacao(form.idOrganizacao());
		this.valorEstimado = form.valorEstimado();
		this.objetivo = form.objetivo();
		this.objetivoEspecifico = form.objetivoEspecifico();
		this.situacaoProblema = form.situacaoProblema();
		this.solucoesPropostas = form.solucoesPropostas();
		this.impactos = form.impactos();
		this.arranjosInstitucionais = form.arranjosInstitucionais();
		this.atualizadoEm = LocalDateTime.now();
	}

	public void apagar() {
		this.sigla = null;
		this.atualizadoEm = LocalDateTime.now();
	}

	public Long getIdEixo() {
		return this.area.getEixo() != null ? this.area.getEixo().getId() : null;
	}

	public Long getIdPlano() {
		if (getIdEixo() == null)
			return null;
		return this.area.getEixo().getPlano() != null ? this.area.getEixo().getPlano().getId() : null;
	}

	public boolean isAtivo() {
		return Objects.equals(this.getStatus().getId(), StatusEnum.ATIVO.getValue());
	}
}
