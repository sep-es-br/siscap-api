package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.EquipeDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "projeto_microrregiao",
				joinColumns = {@JoinColumn(name = "id_projeto")},
				inverseJoinColumns = @JoinColumn(name = "id_microrregiao"))
	private List<Microrregiao> microrregioes;

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL)
	private Set<ProjetoPessoa> projetoPessoaSet;

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
		this.microrregioes = form.idMicrorregioes().stream().map(Microrregiao::new).toList();
		adicionarProjetoPessoa(form.idResponsavelProponente(), form.equipeElaboracao());
		this.criadoEm = LocalDateTime.now();
		this.apagado = Boolean.FALSE;
	}

	public void atualizarProjeto(ProjetoForm form) {
		this.sigla = form.sigla();
		this.titulo = form.titulo();
		this.organizacao = new Organizacao(form.idOrganizacao());
		this.valorEstimado = form.valorEstimado();
		this.microrregioes = form.idMicrorregioes()
					.stream().map(Microrregiao::new).collect(Collectors.toList());
		this.objetivo = form.objetivo();
		this.objetivoEspecifico = form.objetivoEspecifico();
		this.situacaoProblema = form.situacaoProblema();
		this.solucoesPropostas = form.solucoesPropostas();
		this.impactos = form.impactos();
		this.arranjosInstitucionais = form.arranjosInstitucionais();
		editarProjetoPessoa(form.idResponsavelProponente(), form.equipeElaboracao());
		this.atualizadoEm = LocalDateTime.now();
	}

	public void apagar() {
		this.sigla = null;
		this.atualizadoEm = LocalDateTime.now();
	}

	public Long getResponsavelProponente() {
		return buscarResponsavelProponente().getPessoa().getId();
	}

	public List<EquipeDto> getEquipeElaboracao() {
		return buscarMembrosEquipeElaboracao().stream().map(EquipeDto::new).collect(Collectors.toList());
	}

	public Long getIdEixo() {
		return this.area.getEixo() != null ? this.area.getEixo().getId() : null;
	}

	public Long getIdPlano() {
		if (getIdEixo() == null)
			return null;
		return this.area.getEixo().getPlano() != null ? this.area.getEixo().getPlano().getId() : null;
	}

	private void adicionarProjetoPessoa(Long idResponsavelProponente, List<EquipeDto> equipeDtoList) {
		if (this.projetoPessoaSet == null)
			this.projetoPessoaSet = new HashSet<>();

		this.projetoPessoaSet.add(new ProjetoPessoa(this, idResponsavelProponente));

		equipeDtoList.stream()
					.map(equipeDto -> new ProjetoPessoa(this, equipeDto))
					.forEach(this.projetoPessoaSet::add);
	}

	private void editarProjetoPessoa(Long idResponsavelProponente, List<EquipeDto> equipeDtoList) {
		if (this.projetoPessoaSet != null) {
			ProjetoPessoa responsavelProponente = buscarResponsavelProponente();
			Set<ProjetoPessoa> membrosEquipeElaboracaoSet = buscarMembrosEquipeElaboracao();

			if (responsavelProponente != null && !Objects.equals(responsavelProponente.getPessoa().getId(), idResponsavelProponente)) {
				responsavelProponente.atualizar();
				this.projetoPessoaSet.remove(responsavelProponente);
				this.projetoPessoaSet.add(new ProjetoPessoa(this, idResponsavelProponente));
			}

			equipeDtoList.forEach(equipeDto -> {
				membrosEquipeElaboracaoSet.stream()
							.filter(membro -> Objects.equals(membro.getPessoa().getId(), equipeDto.idPessoa()))
							.findFirst()
							.ifPresentOrElse(
										(projetoPessoa) -> {
											if (equipeDto.idStatus() != 1L && equipeDto.justificativa() != null) {
												projetoPessoa.excluirMembro(equipeDto);
												this.projetoPessoaSet.remove(projetoPessoa);
											} else {
												if (!Objects.equals(projetoPessoa.getPapel().getId(), equipeDto.idPapel())) {
													projetoPessoa.atualizar();
													this.projetoPessoaSet.remove(projetoPessoa);
													this.projetoPessoaSet.add(new ProjetoPessoa(this, equipeDto));
												}
											}
										},
										() -> {
											this.projetoPessoaSet.add(new ProjetoPessoa(this, equipeDto));
										}
							);
			});
		}
	}

	private ProjetoPessoa buscarResponsavelProponente() {
		return this.projetoPessoaSet.stream()
					.filter(projetoPessoa -> projetoPessoa.getPapel().getId() == 2L)
					.findFirst()
					.orElse(null);
	}

	private Set<ProjetoPessoa> buscarMembrosEquipeElaboracao() {
		return this.projetoPessoaSet.stream()
					.filter(projetoPessoa -> projetoPessoa.getPapel().getId() != 2L)
					.collect(Collectors.toSet());
	}
}
