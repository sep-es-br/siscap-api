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
public class Projeto extends ControleHistorico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "sigla", length = 12)
	private String sigla;

	@Column(name = "titulo", nullable = false, length = 150)
	private String titulo;

	@Column(name = "valor_estimado", nullable = false, precision = 25, scale = 2)
	private BigDecimal valorEstimado;

	@Column(name = "objetivo", nullable = false, length = 2000)
	private String objetivo;

	@Column(name = "objetivo_especifico", nullable = false, length = 2000)
	private String objetivoEspecifico;

	@ManyToOne
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "status", nullable = false)
	private Status status;

	@ManyToOne
	@JoinColumn(name = "id_organizacao", nullable = false)
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacao;

	@Column(name = "situacao_problema", nullable = false, length = 2000)
	private String situacaoProblema;

	@Column(name = "solucoes_propostas", nullable = false, length = 2000)
	private String solucoesPropostas;

	@Column(name = "impactos", nullable = false, length = 2000)
	private String impactos;

	@Column(name = "arranjos_institucionais", nullable = false, length = 2000)
	private String arranjosInstitucionais;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoMicrorregiao> projetoMicrorregiaoSet;

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
	@Column(name = "data_registro")
	private LocalDateTime dataRegistro;

	public Projeto(Long id) {
		this.setId(id);
	}

	public Projeto(ProjetoForm form) {
		this.setDadosProjeto(form);
	}

	public void atualizarProjeto(ProjetoForm form) {
		this.setDadosProjeto(form);
		super.atualizarHistorico();
	}

	public void apagarProjeto() {
		this.setSigla(null);
		super.apagarHistorico();
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

	private void setDadosProjeto(ProjetoForm form) {
		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());
		this.setValorEstimado(form.valorEstimado());
		this.setObjetivo(form.objetivo());
		this.setObjetivoEspecifico(form.objetivoEspecifico());
		this.setStatus(new Status(StatusEnum.ATIVO.getValue()));
		this.setOrganizacao(new Organizacao(form.idOrganizacao()));
		this.setSituacaoProblema(form.situacaoProblema());
		this.setSolucoesPropostas(form.solucoesPropostas());
		this.setImpactos(form.impactos());
		this.setArranjosInstitucionais(form.arranjosInstitucionais());
	}
}
