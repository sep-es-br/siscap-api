package br.gov.es.siscap.models;

import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.form.ProjetoForm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

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
	private long id;

	@Column(name = "sigla", nullable = false, length = 12)
	private String sigla;

	@Column(name = "titulo", nullable = false, length = 150)
	private String titulo;

	@Column(name = "objetivo", length = 2000)
	private String objetivo;

	@Column(name = "objetivo_especifico", length = 2000)
	private String objetivoEspecifico;

	@ManyToOne
	@SQLJoinTableRestriction("apagado = FALSE")
	@JoinColumn(name = "id_tipo_status", nullable = false)
	private TipoStatus tipoStatus;

	@Column(name = "fase", nullable = false)
	private String fase;

	@Column(name = "rascunho", nullable = false)
	private boolean rascunho;

	@ManyToOne
	@JoinColumn(name = "id_organizacao")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Organizacao organizacao;

	@Column(name = "situacao_problema", length = 2000)
	private String situacaoProblema;

	@Column(name = "solucoes_propostas", length = 2000)
	private String solucoesPropostas;

	@Column(name = "impactos", length = 2000)
	private String impactos;

	@Column(name = "arranjos_institucionais", length = 2000)
	private String arranjosInstitucionais;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoPessoa> projetoPessoaSet = new HashSet<>();

	@OneToMany(mappedBy = "projeto")
	private Set<LocalidadeQuantia> localidadeQuantiaSet = new HashSet<>();

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	@SQLRestriction("""
			apagado_em IS NULL AND
			NOT EXISTS (
			    SELECT 1
			    FROM programa_status ps
			    WHERE ps.id_programa = id_programa
			      AND ps.status = 5
			      AND ps.inicio_em = (
			          SELECT MAX(ps2.inicio_em)
			          FROM programa_status ps2
			          WHERE ps2.id_programa = ps.id_programa
			      )
			)
			""")
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Set<ProjetoPrograma> programaHistorico = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "id_area")
	@SQLJoinTableRestriction("apagado = FALSE")
	private Area area;

	@DateTimeFormat
	@Column(name = "data_registro")
	private LocalDateTime dataRegistro;

	@Column(name = "count_ano", nullable = false)
	private String countAno;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoIndicador> projetoIndicadorSet = new HashSet<>();

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoAcao> projetoAcoesSet = new HashSet<>();

	@Column(name = "pecas_planejamento", length = 2000)
	private String pecasPlanejamento;

	@Column(name = "protocolo_edocs", length = 15)
	private String protocoloEdocs;

	@ManyToOne
	@JoinColumn(name = "id_tipo_motivo_arquivamento")
	@SQLJoinTableRestriction("apagado = FALSE")
	private TipoMotivoArquivamento tipoMotivoArquivamento;

	@Column(name = "justificativa_arquivamento", length = 255)
	private String justificativaArquivamento;

	@Column(name = "id_documento_edocs", length = 50)
	private String idDocumentoCapturadoEdocs;

	@Column(name = "id_processo_edocs", length = 50)
	private String idProcessoEdocs;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoCamposComplementacao> projetoComplementoSet = new HashSet<>();

	@Column(name = "justificativa_exclusao_logica", length = 500)
	private String justificativaExclusaoLogica;

	@OneToMany(mappedBy = "projeto")
	private Set<ProjetoParecer> projetoParecerSet = new HashSet<>();

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter(AccessLevel.NONE)
	private Set<StatusProjeto> historicoStatus = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "id_pessoa_redator")
	private Pessoa pessoa;

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProjetoIndicadorAvulso> projetoIndicadorAvulsoSet = new HashSet<>();

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProjetoOds> ods = new HashSet<>();

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProjetoPlanejamentoPpaLoa> planejamentoPpaLoa = new HashSet<>();

	@Column(name = "nao_previsto_ppa")
	private Boolean naoPrevistoNoPpa;

	public Projeto(Long id) {
		this.setId(id);
	}

	public Projeto(ProjetoForm form) {
		inicializarProjeto();
		atualizarDadosEditaveis(form);
	}

	public void atualizarProjeto(ProjetoForm form) {

		atualizarDadosEditaveis(form);

		super.atualizarHistorico();
	}

	private void inicializarProjeto() {

		this.setTipoStatus(
				new TipoStatus(TipoStatusEnum.ATIVO.getValue()));

		this.setFase("DIC");

		this.setRascunho(true);
	}

	private void atualizarDadosEditaveis(ProjetoForm form) {

		this.setSigla(form.sigla());
		this.setTitulo(form.titulo());

		this.setObjetivo(form.objetivo());
		this.setObjetivoEspecifico(form.objetivoEspecifico());

		this.setOrganizacao(
				form.idOrganizacao() != null
						? new Organizacao(form.idOrganizacao())
						: null);

		this.setSituacaoProblema(form.situacaoProblema());

		this.setSolucoesPropostas(form.solucoesPropostas());

		this.setImpactos(form.impactos());

		this.setArranjosInstitucionais(
				form.arranjosInstitucionais());

		this.setPecasPlanejamento(
				form.pecasPlanejamento());

		this.setProtocoloEdocs(
				form.protocoloEdocs());

		this.setNaoPrevistoNoPpa(
				form.naoPrevistoNoPpa());
	}

	public void apagarProjeto() {

		removerPrograma();

		super.apagarHistorico();
	}

	public Long getIdEixo() {

		return Optional.ofNullable(this.area)
				.map(Area::getEixo)
				.map(eixo -> eixo.getId())
				.orElse(null);
	}

	public Long getIdPlano() {

		return Optional.ofNullable(this.area)
				.map(Area::getEixo)
				.map(eixo -> eixo.getPlano())
				.map(plano -> plano.getId())
				.orElse(null);
	}

	public boolean isAtivo() {

		return this.getTipoStatus() != null
				&& Objects.equals(
						this.getTipoStatus().getId(),
						TipoStatusEnum.ATIVO.getValue());
	}

	public boolean isStatusElegivel() {

		StatusProjeto statusAtual = this.getStatusAtual();

		if (statusAtual == null) {
			return false;
		}

		return Objects.equals(
				statusAtual.getStatus(),
				StatusProjetoEnum.ELEGIVEL.getValue());
	}

	public boolean isElegivelParaVinculo() {

		if (!this.isStatusElegivel()) {
			return false;
		}

		if (this.getPrograma() == null) {
			return true;
		}

		return this.getPrograma().isRecusado()
				|| this.getPrograma().isEmEdicao();
	}

	public void alterarStatus(
			String novoStatus,
			Pessoa pessoa) {

		StatusProjeto statusAtual = this.getStatusAtual();

		if (statusAtual != null
				&& Objects.equals(
						statusAtual.getStatus(),
						novoStatus)) {

			return;
		}

		this.finalizarStatusAtual(pessoa);

		StatusProjeto novoStatusProjeto = StatusProjeto.init(this, novoStatus);

		this.historicoStatus.add(novoStatusProjeto);
	}

	public StatusProjeto finalizarStatusAtual(
			Pessoa pessoa) {

		StatusProjeto statusAtual = this.getStatusAtual();

		if (statusAtual == null) {
			return null;
		}

		return statusAtual.finalizar(pessoa);
	}

	public StatusProjeto getStatusAtual() {

		if (historicoStatus == null
				|| historicoStatus.isEmpty()) {

			return null;
		}

		return historicoStatus.stream()
				.sorted(
						Comparator.comparing(
								StatusProjeto::getInicioEm).reversed())
				.findFirst()
				.orElse(null);
	}

	public Programa getPrograma() {

		return Optional.ofNullable(
				this.getHistoricoAtivo())
				.map(ProjetoPrograma::getPrograma)
				.orElse(null);
	}

	public void removerPrograma() {

		ProjetoPrograma historicoAtivo = this.getHistoricoAtivo();

		if (historicoAtivo == null) {
			return;
		}

		historicoAtivo.setApagadoEm(
				LocalDateTime.now());
	}

	public void setPrograma(
			Programa programa) {

		Assert.notNull(
				programa,
				"Programa não pode ser nulo.");

		Assert.isNull(
				this.getHistoricoAtivo(),
				"Favor remover o programa antes de incluir outro.");

		ProjetoPrograma novo = new ProjetoPrograma(
				this,
				programa);

		this.programaHistorico.add(novo);
	}

	private ProjetoPrograma getHistoricoAtivo() {

		if (programaHistorico == null
				|| programaHistorico.isEmpty()) {

			return null;
		}

		return programaHistorico.stream()
				.filter(
						pp -> pp.getApagadoEm() == null)
				.filter(
						pp -> pp.getPrograma() != null)
				.filter(
						pp -> !pp.getPrograma().isRecusado())
				.findFirst()
				.orElse(null);
	}

	// public void addOds(
	// ProjetoOds ods
	// ) {
	// Assert.notNull(
	// ods,
	// "ODS não pode ser nulo."
	// );
	// ods.setProjeto(this);
	// this.ods.add(ods);
	// }

}